package toast.library.meal;

import android.util.Log;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MealLibrary {
    private static Source mSource;

    public static String[] getDateNew(String CountryCode, String schulCode,
                                      String schulCrseScCode, String schulKndScCode,
                                      String schMmealScCode, String year, String month, String day) {

        String[] date = new String[7];
        String url = "https://stu." + CountryCode
                + "/sts_sci_md01_001.do?schulCode=" + schulCode
                + "&schulCrseScCode=" + schulCrseScCode + "&schulKndScCode="
                + schulKndScCode + "&schMmealScCode=" + schMmealScCode
                + "&schYmd=" + year + "." + month + "." + day;

        return getDateNewSub(date, url);
    }

    private static String[] getDateNewSub(String[] date, String url) {
        try {
            mSource = new Source(new URL(url));
        } catch (MalformedURLException e) {
            Log.e("Exception", e.toString());
        } catch (IOException e) {
            Log.e("Exception", e.toString());
        }

        mSource.fullSequentialParse();
        List<?> table = mSource.getAllElements("table");

        for (int i = 0; i < table.size(); i++) {
            if (((Element) table.get(i)).getAttributeValue("class").equals(
                    "tbl_type3")) {
                List<?> tr = ((Element) table.get(i)).getAllElements("tr");
                List<?> th = ((Element) tr.get(0)).getAllElements("th");

                for (int j = 0; j < 7; j++) {
                    date[j] = ((Element) th.get(j + 1)).getContent().toString();
                }

                break;
            }
        }

        return date;
    }

    public static String[] getMealNew(String CountryCode, String schulCode,
                                      String schulCrseScCode, String schulKndScCode,
                                      String schMmealScCode, String year, String month, String day) {

        String[] content = new String[7];
        String url = "https://stu." + CountryCode
                + "/sts_sci_md01_001.do?schulCode=" + schulCode
                + "&schulCrseScCode=" + schulCrseScCode + "&schulKndScCode="
                + schulKndScCode + "&schMmealScCode=" + schMmealScCode
                + "&schYmd=" + year + "." + month + "." + day;

        return getMealNewSub(content, url);
    }

    private static String[] getMealNewSub(String[] content, String url) {
        try {
            mSource = new Source(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mSource.fullSequentialParse();
        List<?> table = mSource.getAllElements("table");

        for (int i = 0; i < table.size(); i++) {
            if (((Element) table.get(i)).getAttributeValue("class").equals(
                    "tbl_type3")) {
                List<?> tbody = ((Element) table.get(i))
                        .getAllElements("tbody");
                List<?> tr = ((Element) tbody.get(0)).getAllElements("tr");
                List<?> title = ((Element) tr.get(2)).getAllElements("th");

                if (((Element) title.get(0)).getContent().toString()
                        .equals("식재료")) {
                    List<?> tdMeal = ((Element) tr.get(1)).getAllElements("td");

                    for (int j = 0; j < 7; j++) {
                        content[j] = ((Element) tdMeal.get(j)).getContent()
                                .toString();
                        content[j] = content[j].replace("<br />", "\n");
                    }

                    break;
                }

                for (int index = 0; index < content.length; index++) {
                    content[index] = null;
                }

                break;
            }
        }

        return content;
    }

    public static boolean isMealCheck(String meal) {
        return !("".equals(meal) || " ".equals(meal) || meal == null);
    }
}
