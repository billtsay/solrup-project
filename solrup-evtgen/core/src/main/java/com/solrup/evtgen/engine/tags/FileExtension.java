/*
 * Copyright 2014 DataGenerator Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.solrup.evtgen.engine.tags;

import org.apache.commons.scxml2.*;
import org.apache.commons.scxml2.model.Action;
import org.apache.commons.scxml2.model.ModelException;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Implementation of dg:file tag
 */
public class FileExtension implements CustomTagExtension<FileExtension.FileTag> {
    private static final Logger log = Logger.getLogger(FileExtension.class);

    private static Map<String, Set<String>> cachedDataFiles = new HashMap<>();

    public Class<FileTag> getTagActionClass() {
        return FileTag.class;
    }

    public String getTagName() {
        return "file";
    }

    public String getTagNameSpace() {
        return "org.finra.datagenerator";
    }

    /**
     * Performs variable assignments from a set of values
     *
     * @param action a SetAssignTag Action
     * @param possibleStateList a current list of possible states produced so far from expanding a model state
     *
     * @return the cartesian product of every current possible state and the set of values specified by action
     */
    public List<Map<String, String>> pipelinePossibleStates(FileTag action,
                                                            List<Map<String, String>> possibleStateList) {
        String variable = action.getName();
        String fileName = action.getFileName();
        String separator = action.getSeparator();

        Set<String> domain = new HashSet<String>();
        if (fileName != null && fileName.length() > 0) {
            generateFileValues(domain, fileName, separator);
        }

        //take the product
        List<Map<String, String>> productTemp = new LinkedList<>();
        for (Map<String, String> p : possibleStateList) {
            for (String value : domain) {
                HashMap<String, String> n = new HashMap<>(p);
                n.put(variable, value);
                productTemp.add(n);
            }
        }

        return productTemp;
    }

    private void generateFileValues(Set<String> domain, String fileName, String fileSeparator) {
        if (fileName != null && !fileName.equals("")) {
            // check cache with data file's data, maybe this file already cached
            Set<String> data;
            if (cachedDataFiles.containsKey(fileName)) {
                domain.addAll(cachedDataFiles.get(fileName));
            } else {
                data = cacheAndReturnDataFile(fileName, fileSeparator);
                if (data != null) {
                    domain.addAll(data);
                }
            }
        }
    }

    private Set<String> cacheAndReturnDataFile(String fileName, String fileSeparator) {
        InputStream inputStream = FileExtension.class.getResourceAsStream("/" + fileName);
        if (inputStream != null) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                String line;
                List<String> lines = new ArrayList<String>();
                while ((line = bufferedReader.readLine()) != null) {
                    if (!line.startsWith("#")) {
                        lines.add(line);
                    }
                }
                
                Set<String> data = new HashSet<String>();
                if (lines.size() == 1) {
                    if (fileSeparator == null || fileSeparator.equals("")) {
                        fileSeparator = ",";
                    }
                    String[] lineSplitted = lines.get(0).split(fileSeparator);
                    for (String value : lineSplitted) {
                        data.add(value);
                    }
                } else {
                    for (String currentLine : lines) {
                        data.add(currentLine);
                    }
                }
                cachedDataFiles.put(fileName, data);
                return data;
            } catch (IOException e) {
                log.error("Oops! Can't read '" + fileName + "' file... " + e);
            }
        }
        return null;
    }

    /**
     * A custom Action for the 'dg:assign' tag inside models
     */
    public static class FileTag extends Action {
        private String name;
        private String fileName;
        private String separator;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getSeparator() {
            return separator;
        }

        public void setSeparator(String fileSeparator) {
            this.separator = separator;
        }

        @Override
        public void execute(ActionExecutionContext exctx) throws ModelException, SCXMLExpressionException {

        }
    }
}
