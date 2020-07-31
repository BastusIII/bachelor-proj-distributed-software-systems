package vss3.aufgabe5.communication.content;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Content with cities and the distances between them.
 */
public class TableContent implements MessageContent {

    /**
     * The names of all cities.
     */
    private final static ArrayList<String> allCityNames = new ArrayList<>(Arrays.asList(new String[] {
            "Amberg",
            "Ansbach",
            "Aschaffenburg",
            "Augsburg",
            "Bamberg",
            "Bayreuth",
            "Coburg",
            "Erlangen",
            "Fürth",
            "Hof",
            "Ingolstadt",
            "Kaufbeuren",
            "Kempten",
            "Landshut",
            "Memmingen",
            "München",
            "Nürnberg",
            "Passau",
            "Regensburg",
            "Rosenheim",
            "Schwabach",
            "Schweinfurt",
            "Straubing",
            "Weiden",
            "Würzburg"}
    ));

    /**
     * The distances for all known cities.
     */
    private final static int[][] allDistances = {{0, 109, 244, 191, 120, 80, 167, 81, 84, 144, 135, 268, 317, 141, 288, 187, 64, 188, 68, 259, 69, 172, 114, 52, 169},
            {-1, 0, 167, 189, 128, 132, 175, 89, 64, 185, 131, 243, 215, 214, 86, 148, 58, 261, 147, 275, 37, 124, 186, 153, 90},
            {-1, -1, 0, 320, 173, 250, 220, 165, 177, 272, 281, 373, 346, 352, 317, 269, 188, 399, 286, 425, 215, 137, 325, 292, 80},
            {-1, -1, -1, 0, 235, 238, 282, 196, 150, 229, 78, 66, 104, 125, 92, 56, 143, 243, 177, 152, 130, 278, 194, 253, 245},
            {-1, -1, -1, -1, 0, 72, 54, 41, 55, 105, 158, 312, 355, 230, 326, 200, 65, 276, 163, 302, 92, 58, 202, 129, 97},
            {-1, -1, -1, -1, -1, 0, 111, 98, 101, 55, 161, 314, 343, 232, 314, 199, 83, 279, 166, 305, 94, 123, 205, 59, 162},
            {-1, -1, -1, -1, -1, -1, 0, 88, 101, 144, 205, 359, 387, 277, 358, 238, 112, 323, 210, 349, 139, 103, 249, 168, 142},
            {-1, -1, -1, -1, -1, -1, -1, 0, 16, 139, 120, 273, 302, 191, 273, 166, 26, 238, 125, 264, 54, 99, 164, 131, 90},
            {-1, -1, -1, -1, -1, -1, -1, -1, 0, 152, 101, 255, 277, 193, 248, 154, 10, 240, 127, 246, 29, 113, 166, 133, 104},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 212, 376, 394, 249, 365, 242, 134, 296, 175, 367, 146, 155, 222, 94, 194},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 160, 142, 107, 186, 68, 92, 211, 97, 151, 94, 208, 137, 174, 204},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 43, 155, 50, 78, 248, 273, 207, 156, 251, 318, 225, 284, 284},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 164, 31, 107, 200, 252, 195, 136, 186, 259, 211, 256, 232},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 181, 70, 194, 119, 78, 146, 196, 280, 71, 154, 277},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 107, 240, 301, 235, 184, 220, 275, 253, 312, 241},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 149, 146, 103, 52, 137, 233, 108, 174, 219},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 222, 109, 239, 22, 121, 148, 113, 112},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 121, 168, 226, 328, 87, 203, 325},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 198, 111, 213, 45, 85, 210},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 240, 353, 214, 273, 350},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 145, 151, 118, 141},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 261, 179, 47},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 129, 251},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 217},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0}};

    /**
     * The names of the cities.
     */
    private ArrayList<String> cityNames;

    /**
     * The distances between the cities.
     */
    private int[][] distances;

    public ArrayList<String> getCities() {
        return cityNames;
    }

    /** Get the index to a given city.
     * @param city the city.
     * @return -1 if city invalid.
     */
    public int getIndex(final String city) {
        return cityNames.indexOf(city);
    }

    /**  Get the index to a given index.
     * @param index the index.
     * @return null if index out of bounds.
     */
    public String getCity(final int index) {
        try {
            return cityNames.get(index);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public ArrayList<String> getCityNames() {
        return cityNames;
    }

    public void setCityNames(ArrayList<String> cityNames) {
        this.cityNames = cityNames;
    }

    public int[][] getDistances() {
        return distances;
    }

    public void setDistances(int[][] distances) {
        this.distances = distances;
    }

    /**
     * Creates a Table content containing only the cities with given indices.
     * The new table contains only indices from 0 to n.
     * @param cityIndices The indices of the cities contained in the nes table.
     * @return The new table content with selected cities.
     */
    public static TableContent getTableWithCities(final int[] cityIndices) {
        TableContent newTable = new TableContent();
        int[][] distances = new int[cityIndices.length][cityIndices.length];
        ArrayList<String> cityNames = new ArrayList<>();
        int[] cityMapping = new int[cityIndices.length];

        for(int cityIndex: cityIndices) {
            cityNames.add(allCityNames.get(cityIndex));
        }

        for(int i = 0; i < cityIndices.length; i++) {
            for(int j = 0; j < cityIndices.length; j++) {
                distances[i][j] = allDistances[cityIndices[i]][cityIndices[j]];
            }
        }

        newTable.setCityNames(cityNames);
        newTable.setDistances(distances);

       return newTable;
    }

    /**
     * Oh, so beautiful.
     * @return A string representation of the table content.
     */
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Cities: ");
        for(String cityName : cityNames) {
            stringBuffer.append(cityName);
            stringBuffer.append(" ");
        }

        stringBuffer.append("Table:\n");

        for(int i = 0; i < distances.length; ++i) {
            for(int j = 0; j < distances.length; ++j) {
                stringBuffer.append(String.format("%3d", distances[i][j]));
                stringBuffer.append("|");
            }
            stringBuffer.append("\n");
        }

        return stringBuffer.toString();
    }

    public static TableContent getFullContent() {
        TableContent fullContent = new TableContent();
        int[][] content = new int[allDistances.length][allDistances[0].length];
        for(int i = 0; i < allDistances.length; i++) {
            for(int j = 0; j < allDistances[0].length; j++) {
                content[i][j] = allDistances[i][j];
            }
        }
        fullContent.setDistances(content);
        fullContent.setCityNames(new ArrayList<String>(allCityNames));
        return fullContent;
    }
}
