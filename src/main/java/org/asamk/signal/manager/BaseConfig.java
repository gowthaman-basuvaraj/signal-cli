package org.asamk.signal.manager;

import org.whispersystems.libsignal.util.guava.Optional;
import org.whispersystems.signalservice.api.profiles.SignalServiceProfile;
import org.whispersystems.signalservice.api.push.TrustStore;
import org.whispersystems.signalservice.internal.configuration.SignalCdnUrl;
import org.whispersystems.signalservice.internal.configuration.SignalContactDiscoveryUrl;
import org.whispersystems.signalservice.internal.configuration.SignalKeyBackupServiceUrl;
import org.whispersystems.signalservice.internal.configuration.SignalServiceConfiguration;
import org.whispersystems.signalservice.internal.configuration.SignalServiceUrl;
import org.whispersystems.signalservice.internal.configuration.SignalStorageUrl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Dns;
import okhttp3.Interceptor;

public class BaseConfig {

    public final static String PROJECT_NAME = Manager.class.getPackage().getImplementationTitle();
    public final static String PROJECT_VERSION = Manager.class.getPackage().getImplementationVersion();

    final static String USER_AGENT = PROJECT_NAME == null ? "signal-cli" : PROJECT_NAME + " " + PROJECT_VERSION;
    final static String UNIDENTIFIED_SENDER_TRUST_ROOT = "BXu6QIKVz5MA8gstzfOgRQGqyLqOwNKHL6INkv3IHWMF";
    final static int PREKEY_MINIMUM_COUNT = 20;
    final static int PREKEY_BATCH_SIZE = 100;
    final static int MAX_ATTACHMENT_SIZE = 150 * 1024 * 1024;

    private final static String URL = "https://textsecure-service.whispersystems.org";
    private final static String CDN_URL = "https://cdn.signal.org";
    private final static String CDN2_URL = "https://cdn2.signal.org";
    private final static String SIGNAL_KEY_BACKUP_URL = "https://api.backup.signal.org";
    private final static String STORAGE_URL = "https://storage.signal.org";
    private final static TrustStore TRUST_STORE = new WhisperTrustStore();

    private final static Optional<Dns> dns = Optional.absent();

    private final static Interceptor userAgentInterceptor = chain ->
            chain.proceed(chain.request().newBuilder()
                    .header("User-Agent", USER_AGENT)
                    .build());

    private final static List<Interceptor> interceptors = Collections.singletonList(userAgentInterceptor);

    private final static byte[] zkGroupServerPublicParams = new byte[]{};

    final static SignalServiceConfiguration serviceConfiguration = new SignalServiceConfiguration(
            new SignalServiceUrl[]{new SignalServiceUrl(URL, TRUST_STORE)},
            makeSignalCdnUrlMapFor(new SignalCdnUrl[]{new SignalCdnUrl(CDN_URL, TRUST_STORE)}, new SignalCdnUrl[]{new SignalCdnUrl(CDN2_URL, TRUST_STORE)}),
            new SignalContactDiscoveryUrl[0],
            new SignalKeyBackupServiceUrl[]{new SignalKeyBackupServiceUrl(SIGNAL_KEY_BACKUP_URL, TRUST_STORE)},
            new SignalStorageUrl[]{new SignalStorageUrl(STORAGE_URL, TRUST_STORE)},
            interceptors,
            dns,
            zkGroupServerPublicParams
    );

    static final SignalServiceProfile.Capabilities capabilities = new SignalServiceProfile.Capabilities(false, false, false);

    private BaseConfig() {
    }

    private static Map<Integer, SignalCdnUrl[]> makeSignalCdnUrlMapFor(SignalCdnUrl[] cdn0Urls, SignalCdnUrl[] cdn2Urls) {
        Map<Integer, SignalCdnUrl[]> result = new HashMap<>();
        result.put(0, cdn0Urls);
        result.put(2, cdn2Urls);
        return Collections.unmodifiableMap(result);
    }
}
