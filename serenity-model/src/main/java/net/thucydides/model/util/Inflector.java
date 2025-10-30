package net.thucydides.model.util;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

/**
 * Transforms words to singular, plural, humanized (human readable), underscore, camel case, or ordinal form. This is inspired by
 * the <a href="https://api.rubyonrails.org/classes/ActiveSupport/Inflector.html">Inflector</a> class in <a
 * href="http://www.rubyonrails.org">Ruby on Rails</a>, which is distributed under the <a
 * href="http://wiki.rubyonrails.org/rails/pages/License">Rails license</a>.
 *
 * @author Randall Hauch
 */
public class Inflector {

    private static final Inflector INSTANCE = new Inflector();

    public static Inflector getInstance() {
        return INSTANCE;
    }

    public static Inflector inflection() {
        return getInstance();
    }

    public Inflection of(String word) {
        return new Inflection(word, this);
    }

    public MultipleInflection of(int count) {
        return new MultipleInflection(count, this);
    }

    public MultipleInflection of(long count) {
        return new MultipleInflection(count, this);
    }

    protected static class Rule {

        protected final String expression;
        final Pattern expressionPattern;
        final String replacement;

        protected Rule(String expression,
                       String replacement) {
            this.expression = expression;
            this.replacement = replacement;
            this.expressionPattern = Pattern.compile(this.expression, Pattern.CASE_INSENSITIVE);
        }

        /**
         * Apply the rule against the input string, returning the modified string or null if the rule didn't apply (and no
         * modifications were made)
         *
         * @param input the input string
         * @return the modified string if this rule applied, or null if the input was not modified by this rule
         */
        protected String apply(String input) {
            Matcher matcher = this.expressionPattern.matcher(input);
            if (!matcher.find()) return null;
            return matcher.replaceAll(this.replacement);
        }
    }

    private LinkedList<Rule> plurals = new LinkedList<>();
    private LinkedList<Rule> singulars = new LinkedList<>();

    /**
     * The lowercase words that are to be excluded and not processed. This map can be modified by the users via
     */
    private final Set<String> uncountables = new HashSet<>();

    public Inflector() {
        initialize();
    }

    // ------------------------------------------------------------------------------------------------
    // Usage functions
    // ------------------------------------------------------------------------------------------------

    /**
     * Returns the plural form of the word in the string.
     * <p>
     * Examples:
     *
     * <pre>
     *   inflector.pluralize(&quot;post&quot;)               #=&gt; &quot;posts&quot;
     *   inflector.pluralize(&quot;octopus&quot;)            #=&gt; &quot;octopi&quot;
     *   inflector.pluralize(&quot;sheep&quot;)              #=&gt; &quot;sheep&quot;
     *   inflector.pluralize(&quot;words&quot;)              #=&gt; &quot;words&quot;
     *   inflector.pluralize(&quot;the blue mailman&quot;)   #=&gt; &quot;the blue mailmen&quot;
     *   inflector.pluralize(&quot;CamelOctopus&quot;)       #=&gt; &quot;CamelOctopi&quot;
     * </pre>
     * <p>
     * <p>
     * <p>
     * Note that if the {@link Object#toString()} is called on the supplied object, so this method works for non-strings, too.
     *
     * @param word the word that is to be pluralized.
     * @return the pluralized form of the word, or the word itself if it could not be pluralized
     * @see #singularize(Object)
     */
    public String pluralize(Object word) {
        if (word == null) return null;
        String wordStr = word.toString().trim();
        if (wordStr.length() == 0) return wordStr;
        if (isUncountable(wordStr)) return wordStr;
        for (Rule rule : this.plurals) {
            String result = rule.apply(wordStr);
            if (result != null) return result;
        }
        return wordStr;
    }

    public String pluralize(Object word, long count) {
        if (word == null) return null;
        if (count == 1 || count == -1) {
            return word.toString();
        }
        return pluralize(word);
    }

    /**
     * Returns the singular form of the word in the string.
     * <p>
     * Examples:
     *
     * <pre>
     *   inflector.singularize(&quot;posts&quot;)             #=&gt; &quot;post&quot;
     *   inflector.singularize(&quot;octopi&quot;)            #=&gt; &quot;octopus&quot;
     *   inflector.singularize(&quot;sheep&quot;)             #=&gt; &quot;sheep&quot;
     *   inflector.singularize(&quot;words&quot;)             #=&gt; &quot;word&quot;
     *   inflector.singularize(&quot;the blue mailmen&quot;)  #=&gt; &quot;the blue mailman&quot;
     *   inflector.singularize(&quot;CamelOctopi&quot;)       #=&gt; &quot;CamelOctopus&quot;
     * </pre>
     * <p>
     * <p>
     * <p>
     * Note that if the {@link Object#toString()} is called on the supplied object, so this method works for non-strings, too.
     *
     * @param word the word that is to be pluralized.
     * @return the pluralized form of the word, or the word itself if it could not be pluralized
     * @see #pluralize(Object)
     */
    public String singularize(Object word) {
        if (word == null) return null;
        String wordStr = word.toString().trim();
        if (wordStr.length() == 0) return wordStr;
        if (isUncountable(wordStr)) return wordStr;
        for (Rule rule : this.singulars) {
            String result = rule.apply(wordStr);
            if (result != null) return result;
        }
        return wordStr;
    }

    /**
     * Returns a copy of the input with the first character converted to uppercase and the remainder to lowercase.
     *
     * @param words the word to be capitalized
     * @return the string with the first character capitalized and the remaining characters lowercased
     */
    public String capitalize(String words) {
        if (words == null) return null;
        String result = words.trim();
        if (result.length() == 0) return "";
        if (result.length() == 1) return result.toUpperCase();
        return "" + Character.toUpperCase(result.charAt(0)) + result.substring(1).toLowerCase();
    }

    public String firstUpperCase(String words) {
        if (words == null) return null;
        String result = words.trim();
        if (result.length() == 0) return "";
        if (result.length() == 1) return result.toUpperCase();

        StringBuilder capitalisedPhrase = new StringBuilder();
        StringBuilder currentWord = new StringBuilder();
        if (startsWithQuote(words)) { // Starts with a non-black char
            capitalisedPhrase.append(words.charAt(0));
        } else {
            currentWord.append(words.charAt(0));
        }

        for (int i = 1; i < words.length(); i++) {
            if (!isNonBlankSeparator(words.charAt(i))) {
                currentWord.append(words.charAt(i));
            } else {
                capitalisedPhrase
                        .append(StringUtils.capitalize(currentWord.toString()))
                        .append(words.charAt(i));
                currentWord = new StringBuilder();
            }
        }
        capitalisedPhrase.append(StringUtils.capitalize(currentWord.toString()));
        String completeWord = capitalisedPhrase.toString();
        return StringUtils.capitalize(completeWord);
    }

    private boolean startsWithQuote(String word) {
        return ((word.charAt(0) == '\'') || (word.charAt(0) == '"'));
    }

    private final String NON_BLANK_SEPARATORS = "-+_/:;&()";

    private boolean isNonBlankSeparator(char c) {
        return NON_BLANK_SEPARATORS.contains(String.valueOf(c));
    }

    /**
     * Capitalizes the first word and turns underscores into spaces and strips trailing "_id" and any supplied removable tokens.
     * Like {@link #titleCase(String, String[])}, this is meant for creating pretty output.
     * <p>
     * Examples:
     *
     * <pre>
     *   inflector.humanize(&quot;employee_salary&quot;)       #=&gt; &quot;Employee salary&quot;
     *   inflector.humanize(&quot;author_id&quot;)             #=&gt; &quot;Author&quot;
     * </pre>
     *
     * @param lowerCaseAndUnderscoredWords the input to be humanized
     * @param removableTokens              optional array of tokens that are to be removed
     * @return the humanized string
     * @see #titleCase(String, String[])
     */
    public String humanize(String lowerCaseAndUnderscoredWords,
                           String... removableTokens) {

        if (isCamelCase(lowerCaseAndUnderscoredWords)) {
            lowerCaseAndUnderscoredWords = underscore(lowerCaseAndUnderscoredWords);
        }
        String result = humanReadableFormOf(lowerCaseAndUnderscoredWords, removableTokens);

        Set<Acronym> acronyms = Acronym.acronymsIn(result);

        result = result.toLowerCase();

        for (Acronym acronym : acronyms) {
            result = acronym.restoreIn(result);
        }

        return StringUtils.capitalize(result);
    }

    private final static Pattern LOWER_CAMEL_CASE = Pattern.compile("[a-z]+((\\d)|([A-Z0-9][a-z0-9]+))*([A-Z])?");
    private final static Pattern UPPER_CAMEL_CASE = Pattern.compile("^[A-Z][a-zA-Z0-9]*$");

    private boolean isCamelCase(String text) {
        return LOWER_CAMEL_CASE.matcher(text).matches() || UPPER_CAMEL_CASE.matcher(text).matches();
    }

    private String humanReadableFormOf(String lowerCaseAndUnderscoredWords,
                                       String... removableTokens) {
        if (lowerCaseAndUnderscoredWords == null) return null;
        String result = lowerCaseAndUnderscoredWords.trim();
        if (result.length() == 0) return "";
        // Remove a trailing "_id" token
        result = result.replaceAll("_id$", "");
        // Remove all of the tokens that should be removed
        if (removableTokens != null) {
            for (String removableToken : removableTokens) {
                result = result.replaceAll(removableToken, "");
            }
        }
        result = result.replaceAll("_+", " "); // replace all adjacent underscores with a single space

        return result;
    }

    /**
     * Makes an underscored form from the expression in the string method.
     * Also changes any characters that match the supplied delimiters into underscore.
     * <p>
     * Examples:
     *
     * <pre>
     *   inflector.underscore(&quot;activeRecord&quot;)     #=&gt; &quot;active_record&quot;
     *   inflector.underscore(&quot;ActiveRecord&quot;)     #=&gt; &quot;active_record&quot;
     *   inflector.underscore(&quot;firstName&quot;)        #=&gt; &quot;first_name&quot;
     *   inflector.underscore(&quot;FirstName&quot;)        #=&gt; &quot;first_name&quot;
     *   inflector.underscore(&quot;name&quot;)             #=&gt; &quot;name&quot;
     *   inflector.underscore(&quot;The.firstName&quot;)    #=&gt; &quot;the_first_name&quot;
     * </pre>
     *
     * @param camelCaseWord  the camel-cased word that is to be converted;
     * @param delimiterChars optional characters that are used to delimit word boundaries (beyond capitalization)
     * @return a lower-cased version of the input, with separate words delimited by the underscore character.
     */
    public String underscore(String camelCaseWord,
                             char... delimiterChars) {
        if (camelCaseWord == null) return null;
        String result = camelCaseWord.trim();
        if (result.length() == 0) return "";
        result = result.replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2");
        result = result.replaceAll("([a-z\\d])([A-Z])", "$1_$2");
//        result = result.replace('-', '_');
        if (delimiterChars != null) {
            for (char delimiterChar : delimiterChars) {
                result = result.replace(delimiterChar, '_');
            }
        }
        return result.toLowerCase();
    }

    /**
     * Makes an kebab-cased expression of a string method.
     *
     * @param camelCaseWord  the camel-cased word that is to be converted;
     * @param delimiterChars optional characters that are used to delimit word boundaries (beyond capitalization)
     * @return a kebab-cased version of the input, with separate words delimited by the hyphen character.
     */
    public String kebabCase(String camelCaseWord,
                            char... delimiterChars) {
        return underscore(camelCaseWord, delimiterChars).replaceAll("_", "-");
    }

    /**
     * Capitalizes all the words and replaces some characters in the string to create a nicer looking title. Underscores are
     * changed to spaces, a trailing "_id" is removed, and any of the supplied tokens are removed. Like
     * {@link #humanize(String, String[])}, this is meant for creating pretty output.
     * <p>
     * Examples:
     *
     * <pre>
     *   inflector.titleCase(&quot;man from the boondocks&quot;)       #=&gt; &quot;Man From The Boondocks&quot;
     *   inflector.titleCase(&quot;x-men: the last stand&quot;)        #=&gt; &quot;X Men: The Last Stand&quot;
     * </pre>
     *
     * @param words           the input to be turned into title case
     * @param removableTokens optional array of tokens that are to be removed
     * @return the title-case version of the supplied words
     */
    String titleCase(String words,
                     String... removableTokens) {
        String humanizedForm = humanize(words,removableTokens);
        String result = stream(humanizedForm.split("\\s"))
                .map(this::firstUpperCase)
                .collect(Collectors.joining(" "));
        return result.trim();
    }

    // ------------------------------------------------------------------------------------------------
    // Management methods
    // ------------------------------------------------------------------------------------------------

    /**
     * Determine whether the supplied word is considered uncountable by the {@link #pluralize(Object) pluralize} and
     * {@link #singularize(Object) singularize} methods.
     *
     * @param word the word
     * @return true if the plural and singular forms of the word are the same
     */
    private boolean isUncountable(String word) {
        String trimmedLower = word.trim().toLowerCase();
        return this.uncountables.contains(trimmedLower);
    }

    private void addPluralize(String rule,
                              String replacement) {
        final Rule pluralizeRule = new Rule(rule, replacement);
        this.plurals.addFirst(pluralizeRule);
    }

    private void addSingularize(String rule,
                                String replacement) {
        final Rule singularizeRule = new Rule(rule, replacement);
        this.singulars.addFirst(singularizeRule);
    }

    private void addIrregular(String singular,
                              String plural) {
        String singularRemainder = singular.length() > 1 ? singular.substring(1) : "";
        String pluralRemainder = plural.length() > 1 ? plural.substring(1) : "";
        addPluralize("(" + singular.charAt(0) + ")" + singularRemainder + "$", "$1" + pluralRemainder);
        addSingularize("(" + plural.charAt(0) + ")" + pluralRemainder + "$", "$1" + singularRemainder);
    }

    private void addUncountable(String... words) {
        for (String word : words) {
            uncountables.add(word.trim().toLowerCase());
        }
    }

    /**
     * Utility method to replace all occurrences given by the specific backreference with its uppercased form, and remove all
     * other backreferences.
     * <p>
     * The Java {@link Pattern regular expression processing} does not use the preprocessing directives <code>\l</code>,
     * <code>&#92;u</code>, <code>\L</code>, and <code>\U</code>. If so, such directives could be used in the replacement string
     * to uppercase or lowercase the backreferences. For example, <code>\L1</code> would lowercase the first backreference, and
     * <code>&#92;u3</code> would uppercase the 3rd backreference.
     *
     * @param input                  the input string
     * @param regex                  regular expression to replace
     * @param groupNumberToUppercase the regex group to convert to uppercase
     * @return the input string with the appropriate characters converted to upper-case
     */
    private static String replaceAllWithUppercase(String input,
                                                  String regex,
                                                  int groupNumberToUppercase) {
        Pattern underscoreAndDotPattern = Pattern.compile(regex);
        Matcher matcher = underscoreAndDotPattern.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(groupNumberToUppercase).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    protected void initialize() {
        addPluralize("$", "s");
        addPluralize("s$", "s");
        addPluralize("(ax|test)is$", "$1es");
        addPluralize("(octop|vir)us$", "$1i");
        addPluralize("(octop|vir)i$", "$1i"); // already plural
        addPluralize("(alias|status)$", "$1es");
        addPluralize("(bu)s$", "$1ses");
        addPluralize("(buffal|tomat)o$", "$1oes");
        addPluralize("([ti])um$", "$1a");
        addPluralize("([ti])a$", "$1a"); // already plural
        addPluralize("sis$", "ses");
        addPluralize("(?:([^f])fe|([lr])f)$", "$1$2ves");
        addPluralize("(hive)$", "$1s");
        addPluralize("([^aeiouy]|qu)y$", "$1ies");
        addPluralize("(x|ch|ss|sh)$", "$1es");
        addPluralize("(matr|vert|ind)ix|ex$", "$1ices");
        addPluralize("([m|l])ouse$", "$1ice");
        addPluralize("([m|l])ice$", "$1ice");
        addPluralize("^(ox)$", "$1en");
        addPluralize("(quiz)$", "$1zes");
        // Need to check for the following words that are already pluralized:
        addPluralize("(people|men|children|sexes|moves|stadiums)$", "$1"); // irregulars
        addPluralize("(oxen|octopi|viri|aliases|quizzes)$", "$1"); // special rules

        addSingularize("s$", "");
        addSingularize("(s|si|u)s$", "$1s"); // '-us' and '-ss' are already singular
        addSingularize("(n)ews$", "$1ews");
        addSingularize("([ti])a$", "$1um");
        addSingularize("((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)ses$", "$1$2sis");
        addSingularize("(^analy)ses$", "$1sis");
        addSingularize("(^analy)sis$", "$1sis"); // already singular, but ends in 's'
        addSingularize("([^f])ves$", "$1fe");
        addSingularize("(hive)s$", "$1");
        addSingularize("(tive)s$", "$1");
        addSingularize("([lr])ves$", "$1f");
        addSingularize("([^aeiouy]|qu)ies$", "$1y");
        addSingularize("(s)eries$", "$1eries");
        addSingularize("(m)ovies$", "$1ovie");
        addSingularize("(x|ch|ss|sh)es$", "$1");
        addSingularize("([m|l])ice$", "$1ouse");
        addSingularize("(bus)es$", "$1");
        addSingularize("(o)es$", "$1");
        addSingularize("(shoe)s$", "$1");
        addSingularize("(cris|ax|test)is$", "$1is"); // already singular, but ends in 's'
        addSingularize("(cris|ax|test)es$", "$1is");
        addSingularize("(octop|vir)i$", "$1us");
        addSingularize("(octop|vir)us$", "$1us"); // already singular, but ends in 's'
        addSingularize("(alias|status)es$", "$1");
        addSingularize("(alias|status)$", "$1"); // already singular, but ends in 's'
        addSingularize("^(ox)en", "$1");
        addSingularize("(vert|ind)ices$", "$1ex");
        addSingularize("(matr)ices$", "$1ix");
        addSingularize("(quiz)zes$", "$1");

        addIrregular("person", "people");
        addIrregular("man", "men");
        addIrregular("child", "children");
        addIrregular("sex", "sexes");
        addIrregular("move", "moves");
        addIrregular("stadium", "stadiums");

        addUncountable("equipment", "information", "rice", "money", "species", "series", "fish", "sheep");
    }

}
