package com.kyyee.sps.common.component.cache;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * websocket 用户连接信息
 */
@Slf4j
public class UserCache {
    /**
     * 缓存当前websocket连接的信息，包括终端唯一标识和对应的用户usercode
     */
    private static final ConcurrentMap<String, CacheData> cache = new ConcurrentHashMap<>();

    static {
        new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(() -> {
            cache.values().removeIf(CacheData::isExpire);
            log.debug("cache size:{}, clientId:{} and userCode:{}", getAll().size(),
                String.join(",", getAll().keySet()),
                getAllUser().stream().map(User::getUserCode).collect(Collectors.joining(",")));
        }, 0L, 1L, TimeUnit.MINUTES);
    }

    private UserCache() {
    }

    public static boolean isEmpty() {
        return cache.isEmpty();
    }

    public static void set(String key, User user) {
        set(key, user, 0L);
    }

    public static void set(String key, User user, long expire) {
        set(key, user, expire, true);
    }

    public static void set(String key, User user, long expire, boolean autoRenewal) {
        if (!ObjectUtils.isEmpty(user)) {
            cache.put(key, new CacheData(user, expire, autoRenewal));
        }
    }

    public static User get(String key) {
        final CacheData data = cache.get(key);

        return ObjectUtils.isEmpty(data) ? null : data.getUser();

    }

    public static User get(String key, CacheCallback callback) {
        User user = get(key);
        if (ObjectUtils.isEmpty(user) && !ObjectUtils.isEmpty(callback)) {
            user = callback.invoke();
        }
        return user;
    }

    public static void updateByUserId(Long userId, User user) {
        cache.entrySet().stream().filter(entry -> userId.equals(entry.getValue().getUser().getUserId())).forEach(entry -> set(entry.getKey(), user));
    }

    public static void removeByUserId(Long userId) {
        cache.values().removeIf(cacheData -> userId.equals(cacheData.getUser().getUserId()));
    }

    public static void remove(String key) {
        cache.remove(key);
    }

    public static void removeAll() {
        cache.clear();
    }

    public static Map<String, User> getAll() {
        return cache.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
            entry -> entry.getValue().getUser(), (a, b) -> b, () -> new HashMap<>(cache.size())));
    }

    public static List<User> getAllUser() {
        return cache.values().stream().map(CacheData::getUser).toList();
    }

    public static List<User> getByUserCode(String userCode) {
        return cache.values().stream().map(CacheData::getUser)
            .filter(user -> user.getUserCode().equals(userCode)).toList();
    }

    public static List<User> getByUserId(Long userId) {
        return cache.values().stream().map(CacheData::getUser)
            .filter(user -> user.getUserId().equals(userId)).toList();
    }

    public static List<User> getByRoleId(Long roleId) {
        return cache.values().stream().map(CacheData::getUser)
            .filter(user -> user.getRoles().contains(roleId)).toList();
    }

    @Data
    public static class User {

        private Long userId;

        private String userCode;

        private String userName;

        private String userImage;

        private String userType;

        private String address;

        private String birthday;

        private String cellPhone;

        private String email;

        private String expiryTime;

        private String gender;

        private String identityNo;

        private String ipAddress;

        private String reuse;

        private String thirdparty;

        private Long orgId;

        private String orgIndex;

        private String orgName;

        private String priority;

        private List<Long> roles;

        private Short status;

        private String theme;

        private String workPhone;

        // 最后推送时间
        private Instant lastSendTime = Instant.now();

        private Integer outer;
    }

    static class CacheData {

        private final User user;

        // 毫秒
        private final long expire;

        private final boolean autoRenewal;

        private Instant baseTime = Instant.now();

        public CacheData(User user, long expire, boolean autoRenewal) {
            this.user = user;
            this.expire = Math.max(expire, 0L);
            this.autoRenewal = autoRenewal;
        }

        public boolean isExpire() {
            if (this.expire <= 0L) {
                return false;
            } else {
                return ChronoUnit.MILLIS.between(baseTime, Instant.now()) >= this.expire;
            }
        }

        public User getUser() {
            if (this.isExpire()) {
                return null;
            } else {
                if (this.autoRenewal) {
                    this.baseTime = Instant.now();
                }
                return this.user;
            }
        }
    }

    interface CacheCallback {
        User invoke();
    }

}
