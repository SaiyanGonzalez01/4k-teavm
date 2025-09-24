package dev.colbster937.eagler;

import java.util.HashMap;
import java.util.Map;

import org.teavm.interop.Async;
import org.teavm.interop.AsyncCallback;
import org.teavm.jso.typedarrays.ArrayBuffer;

import net.lax1dude.eaglercraft.v1_8.internal.teavm.TeaVMDataURLManager;
import net.lax1dude.eaglercraft.v1_8.internal.teavm.TeaVMFetchJS;
import net.lax1dude.eaglercraft.v1_8.internal.teavm.TeaVMUtils;

public class ResourceAdapter {
	private static final byte[] MISSING_FILE = new byte[0];
	private static boolean hasFetchSupport;
	private static boolean hasDataURLSupport;

	public ResourceAdapter() {
		hasFetchSupport = TeaVMFetchJS.checkFetchSupport();
		hasDataURLSupport = TeaVMDataURLManager.checkDataURLSupport(hasFetchSupport);
	}

	private Map<String,byte[]> assets = new HashMap<>();

	private static boolean isDataURL(String url) {
		return url.length() > 5 && url.substring(0, 5).equalsIgnoreCase("data:");
	}

    public byte[] getResourceBytes(String path) {
		if(path.startsWith("/")) {
			path = path.substring(1);
		}
		byte[] data = assets.get(path);
		return data == MISSING_FILE ? null : data;
	}

	@Async
	private static native ArrayBuffer downloadRemoteURIFetch(final String assetPackageURI, final boolean forceCache);

	private static void downloadRemoteURIFetch(final String assetPackageURI, final boolean useCache, final AsyncCallback<ArrayBuffer> cb) {
		final boolean isDat = isDataURL(assetPackageURI);
		if(isDat && !hasDataURLSupport) {
			cb.complete(TeaVMUtils.unwrapArrayBuffer(TeaVMDataURLManager.decodeDataURLFallback(assetPackageURI)));
			return;
		}
		TeaVMFetchJS.doFetchDownload(assetPackageURI, useCache ? "force-cache" : "no-store",
				isDat ? (data) -> {
					if(data != null) {
						cb.complete(data);
					}else {
						System.err.println("Caught an error decoding data URL via fetch, doing it the slow way instead...");
						byte[] b = null;
						try {
							b = TeaVMDataURLManager.decodeDataURLFallback(assetPackageURI);
						}catch(Throwable t) {
							System.err.println("Failed to manually decode data URL!");
							cb.complete(null);
							return;
						}
						cb.complete(b == null ? null : TeaVMUtils.unwrapArrayBuffer(b));
					}
				} : cb::complete);
	}

	@Async
	private static native ArrayBuffer downloadRemoteURIXHR(final String assetPackageURI);

	private static void downloadRemoteURIXHR(final String assetPackageURI, final AsyncCallback<ArrayBuffer> cb) {
		final boolean isDat = isDataURL(assetPackageURI);
		if(isDat && !hasDataURLSupport) {
			cb.complete(TeaVMUtils.unwrapArrayBuffer(TeaVMDataURLManager.decodeDataURLFallback(assetPackageURI)));
			return;
		}
		TeaVMFetchJS.doXHRDownload(assetPackageURI, isDat ? (data) -> {
					if(data != null) {
						cb.complete(data);
					}else {
						System.err.println("Caught an error decoding data URL via XHR, doing it the slow way instead...");
						byte[] b = null;
						try {
							b = TeaVMDataURLManager.decodeDataURLFallback(assetPackageURI);
						}catch(Throwable t) {
							System.err.println("Failed to manually decode data URL!");
							cb.complete(null);
							return;
						}
						cb.complete(b == null ? null : TeaVMUtils.unwrapArrayBuffer(b));
					}
				} : cb::complete);
	}

	public ArrayBuffer downloadRemoteURI(String assetPackageURI) {
		if(hasFetchSupport) {
			return downloadRemoteURIFetch(assetPackageURI, true);
		}else {
			return downloadRemoteURIXHR(assetPackageURI);
		}
	}
}
