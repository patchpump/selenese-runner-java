package jp.vmi.selenium.webdriver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.google.common.collect.Maps;

/**
 * Options for WebDriver.
 */
public class DriverOptions {

    // private static final Logger log = LoggerFactory.getLogger(DriverOptions.class);

    /**
     * WebDriver option.
     */
    public enum DriverOption {
        /** --profile */
        PROFILE,
        /** --profile-dir */
        PROFILE_DIR,
        /** --proxy */
        PROXY,
        /** --proxy-user */
        PROXY_USER,
        /** --proxy-password */
        PROXY_PASSWORD,
        /** --no-proxy */
        NO_PROXY,
        /** --firefox */
        FIREFOX,
        /** --chromedriver */
        CHROMEDRIVER,
        /** --iedriver */
        IEDRIVER,
        /** --phantomjs */
        PHANTOMJS,
        /** --remote-platform */
        REMOTE_PLATFORM,
        /** --remote-browser */
        REMOTE_BROWSER,
        /** --remote-version */
        REMOTE_VERSION,
        /** --remote-url */
        REMOTE_URL,
        /** --width */
        WIDTH,
        /** --height */
        HEIGHT,
        /** --define */
        DEFINE,
    }

    private final IdentityHashMap<DriverOptions.DriverOption, String> map = Maps.newIdentityHashMap();
    private final DesiredCapabilities caps = new DesiredCapabilities();
    private final HashMap<String, String> envVars = Maps.newHashMap();

    /**
     * Constructs empty options.
     */
    public DriverOptions() {
    }

    /**
     * Constructs driver options specified by command line.
     *
     * @param cli parsed command line information.
     */
    public DriverOptions(CommandLine cli) {
        for (DriverOption opt : DriverOption.values()) {
            String key = opt.name().toLowerCase().replace('_', '-');
            if (opt != DriverOption.DEFINE)
                set(opt, cli.getOptionValue(key));
            else
                addDefinitions(cli.getOptionValues("define"));
        }
    }

    /**
     * Constructs clone of DriverOptions.
     *
     * @param other other DriverOptions.
     */
    public DriverOptions(DriverOptions other) {
        map.putAll(other.map);
        caps.merge(other.caps);
        envVars.putAll(other.envVars);
    }

    /**
     * Get option value.
     *
     * @param opt option key.
     * @return option value.
     */
    public String get(DriverOption opt) {
        if (opt == DriverOption.DEFINE)
            throw new IllegalArgumentException("Need to use DriverOptions#getCapabilities() instead of get(DriverOption.DEFINE).");
        return map.get(opt);
    }

    /**
     * DriverOptions instance has specified option.
     *
     * @param opt option key.
     * @return true if has specified option.
     */
    public boolean has(DriverOption opt) {
        if (opt != DriverOption.DEFINE)
            return map.containsKey(opt);
        else
            return !caps.asMap().isEmpty();
    }

    /**
     * Set option key and value.
     *
     * @param opt option key.
     * @param values option values. (multiple values are accepted by DEFINE only)
     * @return this.
     */
    public DriverOptions set(DriverOption opt, String... values) {
        if (opt != DriverOption.DEFINE) {
            if (values.length != 1)
                throw new IllegalArgumentException("Need to pass only a single value for " + opt);
            if (values[0] != null)
                map.put(opt, values[0]);
            else
                map.remove(opt);
        } else {
            addDefinitions(values);
        }
        return this;
    }

    /**
     * Add "define" parameters.
     *
     * @param defs definitions.
     * @return this.
     */
    public DriverOptions addDefinitions(String... defs) {
        if (defs == null)
            return this;
        for (String def : defs) {
            if (def.contains("+=")) {
                String[] pair = def.split("\\+=", 2);
                String capName = pair[0];
                String capValue = pair[1];
                Object prevCapValue = caps.getCapability(capName);
                if (prevCapValue == null)
                    caps.setCapability(capName, new String[] { capValue });
                else if (prevCapValue instanceof String)
                    caps.setCapability(capName, new String[] { (String) prevCapValue, capValue });
                else if (prevCapValue instanceof String[])
                    caps.setCapability(capName, ArrayUtils.add((String[]) prevCapValue, capValue));
                else
                    throw new IllegalArgumentException("The capability " + capName + " is not string.");
            } else if (def.contains("=")) {
                String[] pair = def.split("=", 2);
                String capName = pair[0];
                String capValue = pair[1];
                caps.setCapability(capName, capValue);
            } else {
                throw new IllegalArgumentException("The definition format need to be KEY=VALUE or KEY+=VALUE: " + def);
            }
        }
        return this;
    }

    /**
     * Get environment variables map.
     *
     * @return environment variables map.
     */
    public Map<String, String> getEnvVars() {
        return envVars;
    }

    private static final Comparator<Entry<String, ?>> mapEntryComparator = new Comparator<Map.Entry<String, ?>>() {

        @Override
        public int compare(Entry<String, ?> e1, Entry<String, ?> e2) {
            return e1.getKey().compareTo(e2.getKey());
        }
    };

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("[");
        String sep = "";
        if (!map.isEmpty()) {
            for (DriverOption opt : DriverOption.values())
                if (opt != DriverOption.DEFINE && map.containsKey(opt))
                    result.append(opt.name()).append('=').append(map.get(opt)).append('|');
            result.deleteCharAt(result.length() - 1);
            sep = "|";
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> capsMap = (Map<String, Object>) caps.asMap();
        if (!capsMap.isEmpty()) {
            result.append(sep).append("DEFINE=[\n");
            List<Entry<String, Object>> capsList = new ArrayList<Entry<String, Object>>(capsMap.entrySet());
            Collections.sort(capsList, mapEntryComparator);
            for (Entry<String, Object> cap : capsList) {
                Object value = cap.getValue();
                if (value instanceof Object[])
                    value = StringUtils.join((Object[]) value, ", ");
                result.append("  ").append(cap.getKey()).append('=').append(value).append("\n");
            }
            result.append(']');
            sep = "|";
        }
        if (!envVars.isEmpty()) {
            result.append(sep).append("ENV_VARS=[\n");
            List<Entry<String, String>> envVarsList = new ArrayList<Entry<String, String>>(envVars.entrySet());
            Collections.sort(envVarsList, mapEntryComparator);
            for (Entry<String, String> envVar : envVarsList)
                result.append("  ").append(envVar.getKey()).append('=').append(envVar.getValue()).append("\n");
            result.append(']');
        }
        result.append(']');
        return result.toString();
    }

    /**
     * Get desired capabilities.
     *
     * @return desired capabilities.
     */
    public DesiredCapabilities getCapabilities() {
        return caps;
    }
}
