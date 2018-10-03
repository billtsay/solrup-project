/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.solrup.gora.jackson.query;

import java.io.IOException;

import org.apache.gora.persistency.impl.PersistentBase;
import org.apache.gora.query.impl.ResultBase;

import com.solrup.gora.jackson.store.JacksonStore;

public class JacksonResult<K, T extends PersistentBase> extends ResultBase<K, T> {

  public JacksonResult(JacksonStore<K,T> dataStore, JacksonQuery<K,T> query) {
    super(dataStore, query);
  }

  public void close() throws IOException {
  }

  @Override
  public float getProgress() throws IOException {
    if (this.limit != -1) {
      return (float) this.offset / (float) this.limit;
    } else {
      return 0;
    }
  }

  @Override
  public boolean nextInner() throws IOException {
    return persistent != null;
  }  

  public int size() {
    return (int) limit;
  }
}
