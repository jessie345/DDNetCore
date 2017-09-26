package com.luojilab.netsupport.netcore.picasso;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.luojilab.netsupport.utils.ClientBuilderUtils;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.StatsSnapshot;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * user liushuo
 * date 2017/3/23
 */

public class PicassoWrapper {
    public static String CACHE_DIR = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/.luojilab_player/";
    public static final String CACHE_OTHERS = CACHE_DIR + "cache/"; //其他缓存文件

    private static PicassoWrapper singleton;

    private Picasso mPicssso;

    private PicassoWrapper(Picasso picasso) {
        mPicssso = picasso;
    }

    /**
     * Cancel any existing requests for the specified target {@link ImageView}.
     */
    @SuppressWarnings("UnusedDeclaration")
    public void cancelRequest(ImageView view) {
        mPicssso.cancelRequest(view);
    }

    /**
     * Cancel any existing requests for the specified {@link Target} instance.
     */
    @SuppressWarnings("UnusedDeclaration")
    public void cancelRequest(Target target) {
        mPicssso.cancelRequest(target);
    }

    /**
     * Cancel any existing requests for the specified {@link RemoteViews} target with the given {@code
     * viewId}.
     */
    @SuppressWarnings("UnusedDeclaration")
    public void cancelRequest(RemoteViews remoteViews, int viewId) {
        mPicssso.cancelRequest(remoteViews, viewId);
    }

    /**
     * Cancel any existing requests with given TAG. You can set a TAG
     * on new requests with {@link RequestCreator#tag(Object)}.
     *
     * @see RequestCreator#tag(Object)
     */
    @SuppressWarnings("UnusedDeclaration")
    public void cancelTag(Object tag) {
        mPicssso.cancelTag(tag);
    }

    /**
     * Pause existing requests with the given TAG. Use {@link #resumeTag(Object)}
     * to resume requests with the given TAG.
     *
     * @see #resumeTag(Object)
     * @see RequestCreator#tag(Object)
     */
    @SuppressWarnings("UnusedDeclaration")
    public void pauseTag(Object tag) {
        mPicssso.pauseTag(tag);
    }

    /**
     * Resume paused requests with the given TAG. Use {@link #pauseTag(Object)}
     * to pause requests with the given TAG.
     *
     * @see #pauseTag(Object)
     * @see RequestCreator#tag(Object)
     */
    @SuppressWarnings("UnusedDeclaration")
    public void resumeTag(Object tag) {
        mPicssso.resumeTag(tag);
    }

    /**
     * Start an image request using the specified URI.
     * <p>
     * Passing {@code null} as a {@code uri} will not trigger any request but will set a placeholder,
     * if one is specified.
     *
     * @see #load(File)
     * @see #load(String)
     * @see #load(int)
     */
    public RequestCreator load(Uri uri) {
        return mPicssso.load(uri);
    }

    /**
     * Start an image request using the specified path. This is a convenience method for calling
     * {@link #load(Uri)}.
     * <p>
     * This path may be a remote URL, file resource (prefixed with {@code file:}), content resource
     * (prefixed with {@code content:}), or android resource (prefixed with {@code
     * android.resource:}.
     * <p>
     * Passing {@code null} as a {@code path} will not trigger any request but will set a
     * placeholder, if one is specified.
     *
     * @throws IllegalArgumentException if {@code path} is empty or blank string.
     * @see #load(Uri)
     * @see #load(File)
     * @see #load(int)
     */
    public RequestCreator load(String path) {
        path = TextUtils.isEmpty(path) ? null : path;
        return mPicssso.load(path);
    }

    /**
     * Start an image request using the specified image file. This is a convenience method for
     * calling {@link #load(Uri)}.
     * <p>
     * Passing {@code null} as a {@code file} will not trigger any request but will set a
     * placeholder, if one is specified.
     * <p>
     * Equivalent to calling {@link #load(Uri) load(Uri.fromFile(file))}.
     *
     * @see #load(Uri)
     * @see #load(String)
     * @see #load(int)
     */
    public RequestCreator load(File file) {
        return mPicssso.load(file);
    }

    /**
     * Start an image request using the specified drawable resource ID.
     *
     * @see #load(Uri)
     * @see #load(String)
     * @see #load(File)
     */
    public RequestCreator load(int resourceId) {
        return mPicssso.load(resourceId);
    }

    /**
     * Invalidate all memory cached images for the specified {@code uri}.
     *
     * @see #invalidate(String)
     * @see #invalidate(File)
     */
    public void invalidate(Uri uri) {
        mPicssso.invalidate(uri);
    }

    /**
     * Invalidate all memory cached images for the specified {@code path}. You can also pass a
     * {@linkplain RequestCreator#stableKey stable key}.
     *
     * @see #invalidate(Uri)
     * @see #invalidate(File)
     */
    public void invalidate(String path) {
        mPicssso.invalidate(path);
    }

    /**
     * Invalidate all memory cached images for the specified {@code file}.
     *
     * @see #invalidate(Uri)
     * @see #invalidate(String)
     */
    public void invalidate(File file) {
        mPicssso.invalidate(file);
    }

    /**
     * {@code true} if debug display, logging, and statistics are enabled.
     * <p>
     *
     * @deprecated Use {@link #areIndicatorsEnabled()} and {@link #isLoggingEnabled()} instead.
     */
    @SuppressWarnings("UnusedDeclaration")
    @Deprecated
    public boolean isDebugging() {
        return mPicssso.isDebugging();
    }

    /**
     * Toggle whether debug display, logging, and statistics are enabled.
     * <p>
     *
     * @deprecated Use {@link #setIndicatorsEnabled(boolean)} and {@link #setLoggingEnabled(boolean)}
     * instead.
     */
    @SuppressWarnings("UnusedDeclaration")
    @Deprecated
    public void setDebugging(boolean debugging) {
        mPicssso.setDebugging(debugging);
    }

    /**
     * Toggle whether to display debug indicators on images.
     */
    @SuppressWarnings("UnusedDeclaration")
    public void setIndicatorsEnabled(boolean enabled) {
        mPicssso.setIndicatorsEnabled(enabled);
    }

    /**
     * {@code true} if debug indicators should are displayed on images.
     */
    @SuppressWarnings("UnusedDeclaration")
    public boolean areIndicatorsEnabled() {
        return mPicssso.areIndicatorsEnabled();
    }

    /**
     * Toggle whether debug logging is enabled.
     * <p>
     * <b>WARNING:</b> Enabling this will result in excessive object allocation. This should be only
     * be used for debugging Picasso behavior. Do NOT pass {@code BuildConfig.DEBUG}.
     */
    @SuppressWarnings("UnusedDeclaration") // Public API.
    public void setLoggingEnabled(boolean enabled) {
        mPicssso.setLoggingEnabled(enabled);
    }

    /**
     * {@code true} if debug logging is enabled.
     */
    @SuppressWarnings("UnusedDeclaration")
    public boolean isLoggingEnabled() {
        return mPicssso.isLoggingEnabled();
    }

    /**
     * Creates a {@link StatsSnapshot} of the current stats for this instance.
     * <p>
     * <b>NOTE:</b> The snapshot may not always be completely up-to-date if requests are still in
     * progress.
     */
    @SuppressWarnings("UnusedDeclaration")
    public StatsSnapshot getSnapshot() {
        return mPicssso.getSnapshot();
    }

    /**
     * Stops this instance from accepting further requests.
     */
    @SuppressWarnings("UnusedDeclaration")
    public void shutdown() {
        mPicssso.shutdown();
    }

    /**
     * The global default {@link Picasso} instance.
     * <p>
     * This instance is automatically initialized with defaults that are suitable to most
     * implementations.
     * <ul>
     * <li>LRU memory cache of 15% the available application RAM</li>
     * <li>Disk cache of 2% storage space up to 50MB but no less than 5MB. (Note: this is only
     * available on API 14+ <em>or</em> if you are using a standalone library that provides a disk
     * cache on all API levels like OkHttp)</li>
     * <li>Three download threads for disk and network access.</li>
     * </ul>
     * <p>
     * If these settings do not meet the requirements of your application you can construct your own
     * with full control over the configuration by using {@link Picasso.Builder} to create a
     * {@link Picasso} instance. You can either use this directly or by setting it as the global
     * instance with {@link #setSingletonInstance}.
     */
    public static PicassoWrapper with(Context context) {
        if (singleton == null) {
            synchronized (PicassoWrapper.class) {
                if (singleton == null) {
                    File cacheDir = new File(CACHE_OTHERS);
                    if (!cacheDir.exists()) {
                        cacheDir.mkdirs();
                    }

                    OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
                    clientBuilder.retryOnConnectionFailure(true)
                            .followSslRedirects(true)
                            .followRedirects(true)
                            .readTimeout(15, TimeUnit.SECONDS)
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .cache(new Cache(cacheDir, 100 * 1024 * 1024))//100m disk cache
                            .addNetworkInterceptor(new CacheInterceptor());

                    ClientBuilderUtils.decorateClientBuilder(clientBuilder);
                    Downloader downloader = new OkHttp3Downloader(clientBuilder.build());
                    Picasso.Builder builder = new Picasso.Builder(context);
                    builder.downloader(downloader);

                    Picasso picasso = builder.build();
                    Picasso.setSingletonInstance(picasso);

                    singleton = new PicassoWrapper(picasso);
                }
            }
        }
        return singleton;
    }

    /**
     * Set the global instance returned from {@link #with}.
     * <p>
     * This method must be called before any calls to {@link #with} and may only be called once.
     */
    public static void setSingletonInstance(PicassoWrapper picassoWrapper) {
        synchronized (PicassoWrapper.class) {
            if (singleton != null) {
                throw new IllegalStateException("Singleton instance already exists.");
            }
            singleton = picassoWrapper;
        }
    }

    static class CacheInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);

            Response responseCacheable = response.newBuilder()
                    .removeHeader("Cache-Control")
                    //cache for 30 days
                    .header("Cache-Control", "public,max-age=" + 3600 * 24 * 30)
                    .build();
            return responseCacheable;
        }
    }
}
