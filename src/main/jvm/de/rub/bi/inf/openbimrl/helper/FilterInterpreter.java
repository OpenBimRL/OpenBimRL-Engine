package de.rub.bi.inf.openbimrl.helper;

import java.util.*;

/**
 * A helper class to handle filter operators and functions for the entire project.
 *
 * @author Marcel Stepien
 */
public class FilterInterpreter {

    private static int checkFilterMapSizes(HashMap<String, ArrayList<?>> filterMap) {
        int size = -1;
        for (ArrayList<?> filter : filterMap.values()) {
            if (filter == null) continue;
            if (size == -1) {
                size = filter.size();
                continue;
            }

            if (size != filter.size()) {
                return -1;
            }

        }
        return size;
    }

    private static HashMap<String, ArrayList<?>> buildFilterMap(String filterStr, Map<String, Object> ruleIDtoValueMap) {
        HashMap<String, ArrayList<?>> filterMap = new HashMap<String, ArrayList<?>>();

        List<String> filterOrArr = new ArrayList<String>();
        if (filterStr.contains(" OR ")) {
            filterOrArr = Arrays.asList(filterStr.split(" OR "));
        } else {
            filterOrArr.add(filterStr);
        }

        for (String filterOrInst : filterOrArr) {

            List<String> filterAndArr = new ArrayList<String>();
            if (filterOrInst.contains(" AND ")) {
                filterAndArr = Arrays.asList(filterOrInst.split(" AND "));
            } else {
                filterAndArr.add(filterOrInst);
            }

            for (String filterAndInst : filterAndArr) {

                String filterStrip = filterAndInst.replace("!", "").replace(" ", ""); //perform negation, if provided
                filterMap.put(filterStrip, (ArrayList<?>) ruleIDtoValueMap.get(filterStrip));
            }
        }
        return filterMap;
    }


    public static ArrayList<Boolean> interpret(String filterStr, Map<String, Object> ruleIDtoValueMap) {

        ArrayList<Boolean> result = new ArrayList<Boolean>();

        HashMap<String, ArrayList<?>> filterMap = FilterInterpreter.buildFilterMap(filterStr, ruleIDtoValueMap);
        int mapSize = checkFilterMapSizes(filterMap);
        if (mapSize == -1) {
            System.err.println("Filter Sizes do not match!");
            return null;
        }

        for (int o = 0; o < mapSize; o++) {

            boolean checkOr = false;

            List<String> filterOrArr = new ArrayList<String>();
            if (filterStr.contains(" OR ")) {
                filterOrArr = Arrays.asList(filterStr.split(" OR "));
            } else {
                filterOrArr.add(filterStr);
            }

            for (String filterOrInst : filterOrArr) {

                boolean checkAnd = true;

                List<String> filterAndArr = new ArrayList<String>();
                if (filterOrInst.contains(" AND ")) {
                    filterAndArr = Arrays.asList(filterOrInst.split(" AND "));
                } else {
                    filterAndArr.add(filterOrInst);
                }

                for (String filterAndInst : filterAndArr) {

                    boolean checkNegation = filterAndInst.startsWith("!"); //perform negation, if provided
                    String filterStrip = filterAndInst.replace("!", "").replace(" ", "");

                    if (filterMap.get(filterStrip).get(o) != null) {
                        checkAnd = checkAnd && (checkNegation != Boolean.valueOf(filterMap.get(filterStrip).get(o).toString())
                        );
                    } else {
                        checkAnd = false;
                    }


                }
                checkOr = checkOr | checkAnd;

            }
            result.add(checkOr);
        }

        return result.isEmpty() ? null : result;
    }

}
