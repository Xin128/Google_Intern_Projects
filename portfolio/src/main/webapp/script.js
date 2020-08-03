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

const DEFAULT_MAX_COMMENT_NUM = 1;
const DISPLAY = "block";
const HIDE = "none";
const LOGGED_IN = 1;
const linebreak = document.createElement('br');

google.charts.load("current", {"packages": ["geochart"]});
google.charts.setOnLoadCallback(drawChart);
/**
 * Adds a random description to the page.
 */
function addRandomDescription() {
  const descriptions = ["Noogler!", "Dog Girl", "Programmer", "Food Lover"];

  // Pick a random description.
  const description =
    descriptions[Math.floor(Math.random() * descriptions.length)];

  // Add it to the page.
  const descriptionContainer = document.getElementById("description-container");
  descriptionContainer.innerText = description;
}

function getCommentInForm() {
  // get number of limited comments from input form
  inputVal = document.getElementById("quantity").value;
  var numComments = (inputVal != '') ? inputVal : DEFAULT_MAX_COMMENT_NUM;
  var url = "/data?numComment=" + numComments;

  /* fetch from data url, then fetch from remote blobstore url with image and display it on the webpage
   * Note: commentMap data structure:
   * {UserName1: [User1Comment1, User1Comment2...],
   * UserName2: [User2Comment1, User2Comment2...],...}
   */
  var commentContainer = document.getElementById('comment-container');
  fetch(url).then(response => response.json()).then((commentMap) => {
    Object.entries(commentMap).forEach(([commentUser,commentEntity]) => {
      console.log(commentEntity);
      var userEmail = document.createTextNode(commentUser.concat(': '));
      commentContainer.append(userEmail);
      commentEntity.forEach(commentContent => {
        console.log(commentContent);
        var commentMsg = document.createTextNode(commentContent.commentMsg);
        commentContainer.append(commentMsg);
        commentContainer.append(linebreak);
        fetch(commentContent.imageUrl).then(blobResponse => blobResponse.blob()).then((bloburl) => {
          var objectURL = URL.createObjectURL(bloburl);
          var imgElem = document.createElement('img');
          imgElem.src = objectURL;
          commentContainer.append(imgElem);
          commentContainer.append(linebreak);
        });
      });
    })
  }); 
};

// delete all the comments from Datastore
function deleteAllComments() {
  const params = new URLSearchParams();
  fetch("/delete-data", { method: "POST", body: params });
}

// fetch the url of image blobstore and show the form to upload files
function fetchBlobstoreUrlAndShowForm() {
  const messageForm = document.getElementById('my-form');
  fetch("/blob").then((response) => response.text())
      .then((imageUploadUrl) => {
        messageForm.action = imageUploadUrl;
        messageForm.classList.remove('hidden');
      });
}

/**
 * The function is to fetch a Promise from "/userLogIn" page, convert the logIn status message
 * into text and add them into the fetch-container displayed on the webpage. 
 * Note: when user has already logged in, his loginfo status is 1; otherwise 0;
 */
function getUserLogInStatus() {
  fetch("/userLogIn")
    .then((response) =>response.json())
    .then((loginfo) => {
      if (loginfo.status == LOGGED_IN) {
          document.getElementById("form-blk").style.display = DISPLAY;
          document.getElementById("userInfo-container").innerHTML = 
              "<p> Hello " + loginfo.email + "! <br> You have already logged in. </p> <p>Logout <a href=\"" 
              + loginfo.logoutUrl + "\">here</a>.</p>"
      } else {
          document.getElementById("form-blk").style.display = HIDE;
          document.getElementById("userInfo-container").innerHTML = 
            "<p> Hello Stranger</p> <p>Please <a href=\"" + loginfo.loginUrl + "\">login</a> first.</p>"
      }
    });
}

/** Creates a chart and adds it to the page. */
function drawChart() {
  const data = new google.visualization.DataTable();
  data.addColumn('string', 'Country');
  data.addColumn('number', 'Days I have stayed');
        data.addRows([
          ['China', 5475],
          ['Germany', 20],
          ['United States', 930],
          ['England', 3],
          ['France', 5],
          ['Italy', 5],
          ['Australia', 10],
          ['South Korea', 50]
        ]);

  const options = {
    'title': 'Countries I have stayed',
    'width':500,
    'height':400
  };

  const chart = new google.visualization.GeoChart(
      document.getElementById('chart-container'));
  chart.draw(data,options);
}