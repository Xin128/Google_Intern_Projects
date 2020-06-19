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

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CommentListResponse;
import com.google.api.services.youtube.model.CommentThreadListResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collection;

/**
  * Servlet that returns comments in database. 
  * Note: We first added the Gson library dependency to pom.xml.
  */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  protected static final String COMMENT = "Comment";
  private final String CONTENT_PROPERTY = "content";
  private final int DEFAULT_MAX_COMMENT_NUM = 1;
  private final String INPUT_MSG_FORM = "comment-input";
  private final String NUM_COMMENT_FORM = "numComment";
  private final String TIMESTAMP_PROPERTY = "timestamp";
  private final String USEREMAIL_PROPERTY = "userEmail";

  private static final String DEVELOPER_KEY = "";
  private static final String APPLICATION_NAME = "API code samples";
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();


    /**
        * Build and return an authorized API client service.
        *
        * @return an authorized API client service
        * @throws GeneralSecurityException, IOException
        */
    public static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new YouTube.Builder(httpTransport, JSON_FACTORY, null)
            .setApplicationName(APPLICATION_NAME)
            .build();
    }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
        YouTube youtubeService = getService();            
        System.out.println(youtubeService);
        YouTube.CommentThreads.List youtuberequest = youtubeService.commentThreads()
            .list("snippet");        
        System.out.println(youtuberequest);
        CommentThreadListResponse youtuberesponse = youtuberequest.setKey(DEVELOPER_KEY)
            .setVideoId("31dYohFK0Tc")
            .setMaxResults(100L)
            .setOrder("time")
            .setTextFormat("plainText")
            .execute();
            System.out.println(youtuberesponse);
    } catch (GeneralSecurityException | IOException e) {
        System.out.println(e.getMessage());
        System.out.println("getting failure");
        return;
    }


    // Get the limiting number of comments
    String requestedNumComment = request.getParameter(NUM_COMMENT_FORM);
    if (requestedNumComment == null) {
        return; 
    }
    int maxNumComments = Integer.parseInt(requestedNumComment);
    
    // Create the query and prepared query to load comment entities from database
    Query query = new Query(COMMENT).addSort(TIMESTAMP_PROPERTY, SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // Add limited number of comment contents and user email to the userComment Map  
    HashMap<String, ArrayList<String>> userComment = new HashMap<String, ArrayList<String>>();
    for (Entity commentEntity : results.asList(FetchOptions.Builder.withLimit(maxNumComments))) {
        String commentMsg = (String) commentEntity.getProperty(CONTENT_PROPERTY);
        String commentUser = (String) commentEntity.getProperty(USEREMAIL_PROPERTY);
        ArrayList<String> msglist = userComment.get(commentUser);
        if (msglist == null) {
            userComment.put(commentUser, new ArrayList<String>(Arrays.asList(commentMsg)));
        } else {
            msglist.add(commentMsg);
        }
    }
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(userComment));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    String inputMsg = request.getParameter(INPUT_MSG_FORM);
    if (inputMsg == null) {
        return; 
    }
    
    if (!inputMsg.isEmpty()) {
      // Create an entity with received comment message
      long timestamp = System.currentTimeMillis();
      Entity commentEntity = new Entity(COMMENT);
      UserService userService = UserServiceFactory.getUserService();      
      String userEmail = userService.getCurrentUser().getEmail();
      commentEntity.setProperty(CONTENT_PROPERTY, inputMsg);
      commentEntity.setProperty(TIMESTAMP_PROPERTY,timestamp);
      commentEntity.setProperty(USEREMAIL_PROPERTY, userEmail);

    // Used Datastore survice to store newly created comment entity
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
    }
    // Redirect back to the current page
    response.sendRedirect("/index.html");
  }

}
