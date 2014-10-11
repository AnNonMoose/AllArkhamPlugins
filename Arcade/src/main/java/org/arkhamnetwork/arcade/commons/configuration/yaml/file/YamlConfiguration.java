package org.arkhamnetwork.arcade.commons.configuration.yaml.file;

import org.arkhamnetwork.arcade.commons.configuration.yaml.Configuration;
import org.arkhamnetwork.arcade.commons.configuration.yaml.ConfigurationSection;
import org.arkhamnetwork.arcade.commons.configuration.yaml.InvalidConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.representer.Representer;

/**
 * An implementation of {@link Configuration} which saves all files in Yaml.
 * Note that this implementation is not synchronized.
 */
public class YamlConfiguration extends FileConfiguration {

    protected static final String COMMENT_PREFIX = "# ";
    protected static final String BLANK_CONFIG = "{}\n";
    private final DumperOptions yamlOptions = new DumperOptions();
    private final Representer yamlRepresenter = new YamlRepresenter();
    private final Yaml yaml = new Yaml(new YamlConstructor(), yamlRepresenter,
            yamlOptions);

    @Override
    public String saveToString() {
        yamlOptions.setIndent(options().indent());
        yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        StringBuilder builder = new StringBuilder(buildHeader());
        if (builder.length() > 0) {
            builder.append('\n'); // newline after header, if present.
        }

        builder.append(saveConfigSectionWithComments(this, false));

        String dump = builder.toString();

        if (dump.equals(BLANK_CONFIG)) {
            dump = "";
        }

        return dump;
    }

    public String saveConfigSectionWithComments(ConfigurationSection section,
            boolean depth) {
        StringBuilder builder = new StringBuilder();
        for (Iterator<Map.Entry<String, Object>> i = section.getValues(false)
                .entrySet().iterator(); i.hasNext();) {
            Map.Entry<String, Object> entry = i.next();

            // Output comment, if present
            String comment = this.getComment(entry.getKey());
            if (comment != null) {
                builder.append(buildComment(comment));
            }

            if (entry.getValue() instanceof ConfigurationSection) {
                // output new line before ConfigurationSection. Pretier.
                builder.append('\n');
				// If it's a section, get it's string representation and append
                // it to our builder.
                // If the first character isn't alphanumeric, quote it !
                if (Character.isLetterOrDigit(entry.getKey().codePointAt(0))) {
                    builder.append(entry.getKey());
                } else {
                    builder.append("'" + entry.getKey() + "'");
                }
                builder.append(":" + yamlOptions.getLineBreak().getString());
                builder.append(saveConfigSectionWithComments(
                        (ConfigurationSection) entry.getValue(), true));
                // output new line after ConfigurationSection. Prettier.
                builder.append('\n');
            } else {
				// If it's not a configuration section, just let yaml do it's
                // stuff.
                builder.append(yaml.dump(Collections.singletonMap(
                        entry.getKey(), entry.getValue())));
            }
        }
        String dump = builder.toString();

        // Prepend the indentation if we aren't the root configuration.
        if (depth) {
            String[] lines = dump.split(Pattern.quote(yamlOptions
                    .getLineBreak().getString()));
            StringBuilder indented = new StringBuilder();
            for (int i = 0; i < lines.length; i++) {
                for (int indent = 0; indent < yamlOptions.getIndent(); indent++) {
                    indented.append(" ");
                }
                indented.append(lines[i]
                        + yamlOptions.getLineBreak().getString());
            }
            return indented.toString();
        } else {
            return dump;
        }
    }

    @Override
    public void loadFromString(String contents)
            throws InvalidConfigurationException {
        Preconditions.checkNotNull(contents, "Contents cannot be null");

        Map<?, ?> input;
        try {
            input = (Map<?, ?>) yaml.load(contents);
        } catch (YAMLException e) {
            throw new InvalidConfigurationException(e);
        } catch (ClassCastException e) {
            throw new InvalidConfigurationException("Top level is not a Map.");
        }

        String header = parseHeader(contents);
        if (header.length() > 0) {
            options().header(header);
        }

        if (input != null) {
            convertMapsToSections(input, this);
        }
    }

    protected void convertMapsToSections(Map<?, ?> input,
            ConfigurationSection section) {
        for (Map.Entry<?, ?> entry : input.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();

            if (value instanceof Map) {
                convertMapsToSections((Map<?, ?>) value,
                        section.createSection(key));
            } else {
                section.set(key, value);
            }
        }
    }

    protected String parseHeader(String input) {
        String[] lines = input.split("\r?\n", -1);
        StringBuilder result = new StringBuilder();
        boolean readingHeader = true;
        boolean foundHeader = false;

        for (int i = 0; (i < lines.length) && (readingHeader); i++) {
            String line = lines[i];

            if (line.startsWith(COMMENT_PREFIX)) {
                if (i > 0) {
                    result.append("\n");
                }

                if (line.length() > COMMENT_PREFIX.length()) {
                    result.append(line.substring(COMMENT_PREFIX.length()));
                }

                foundHeader = true;
            } else if ((foundHeader) && (line.length() == 0)) {
                result.append("\n");
            } else if (foundHeader) {
                readingHeader = false;
            }
        }

        return result.toString();
    }

    @Override
    protected String buildHeader() {
        String header = options().header();

        if (options().copyHeader()) {
            Configuration def = getDefaults();

            if ((def != null) && (def instanceof FileConfiguration)) {
                FileConfiguration filedefaults = (FileConfiguration) def;
                String defaultsHeader = filedefaults.buildHeader();

                if ((defaultsHeader != null) && (defaultsHeader.length() > 0)) {
                    return defaultsHeader;
                }
            }
        }

        if (header == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        String[] lines = header.split("\r?\n", -1);
        boolean startedHeader = false;

        for (int i = lines.length - 1; i >= 0; i--) {
            builder.insert(0, "\n");

            if ((startedHeader) || (lines[i].length() != 0)) {
                builder.insert(0, lines[i]);
                builder.insert(0, COMMENT_PREFIX);
                startedHeader = true;
            }
        }

        return builder.toString();
    }

    @Override
    public YamlConfigurationOptions options() {
        if (options == null) {
            options = new YamlConfigurationOptions(this);
        }

        return (YamlConfigurationOptions) options;
    }

    /**
     * Creates a new {@link YamlConfiguration}, loading from the given file.
     * <p />
     * Any errors loading the Configuration will be logged and then ignored. If
     * the specified input is not a valid config, a blank config will be
     * returned.
     *
     * @param file Input file
     * @return Resulting configuration
     * @throws IllegalArgumentException Thrown if file is null
     */
    public static YamlConfiguration loadConfiguration(File file) {
        Preconditions.checkNotNull(file, "File cannot be null");

        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(file);
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } catch (InvalidConfigurationException ex) {
        }

        return config;
    }

    /**
     * Creates a new {@link YamlConfiguration}, loading from the given stream.
     * <p />
     * Any errors loading the Configuration will be logged and then ignored. If
     * the specified input is not a valid config, a blank config will be
     * returned.
     *
     * @param stream Input stream
     * @return Resulting configuration
     * @throws IllegalArgumentException Thrown if stream is null
     */
    public static YamlConfiguration loadConfiguration(InputStream stream) {
        Preconditions.checkNotNull(stream, "Stream cannot be null");

        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(stream);
        } catch (IOException ex) {
        } catch (InvalidConfigurationException ex) {
        }

        return config;
    }

    /**
     * Format a multi-line property comment.
     *
     * @param comment the original comment string
     * @return the formatted comment string
     */
    protected String buildComment(String comment) {
        StringBuilder builder = new StringBuilder();
        for (String line : comment.split("\r?\n")) {
            builder.append(COMMENT_PREFIX);
            builder.append(line);
            builder.append('\n');
        }
        return builder.toString();
    }
}
