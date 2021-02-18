package wang.wangby.utils;

public class HtmlUtil {
    private static final String[] characterToEntityReferenceMap = new String[3000];

    public static String htmlEscape(String input) {
        return htmlEscape(input, "UTF-8");
    }

    public static String htmlEscape(String input, String encoding) {
        if (input == null) {
            return null;
        } else {
            StringBuilder escaped = new StringBuilder(input.length() * 2);

            for(int i = 0; i < input.length(); ++i) {
                char character = input.charAt(i);
                String reference = convertToReference(character, encoding);
                if (reference != null) {
                    escaped.append(reference);
                } else {
                    escaped.append(character);
                }
            }

            return escaped.toString();
        }
    }

    public static String convertToReference(char character, String encoding) {
        if (encoding.startsWith("UTF-")){
            switch (character){
                case '<':
                    return "&lt;";
                case '>':
                    return "&gt;";
                case '"':
                    return "&quot;";
                case '&':
                    return "&amp;";
                case '\'':
                    return "&#39;";
            }
        }
        else if (character < 1000 || (character >= 8000 && character < 10000)) {
            int index = (character < 1000 ? character : character - 7000);
            String entityReference = characterToEntityReferenceMap[index];
            if (entityReference != null) {
                return entityReference;
            }
        }
        return null;
    }
}
