// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that deletes comments in database. Note: We first added the Gson
 * library dependency to pom.xml.
 */
@WebServlet("/delete-data")
public class DeleteServlet extends HttpServlet {
  String commentStr = DataServlet.COMMENT;

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    Query query = new Query(commentStr);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    for (Entity comment : results.asIterable()) {
      Key commentEntityKey = comment.getKey();
      datastore.delete(commentEntityKey);
    }
    // Redirect back to the current page
    response.sendRedirect("/index.html");
  }
}

