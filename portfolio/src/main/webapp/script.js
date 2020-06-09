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

const DEFAULT_MAX_COMMENT_NUM = 1
const DISPLAY = "block";
const HIDE = "none";

google.charts.load("current", {"packages":["geochart"]});
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
  inputVal = document.getElementById("quantity").value;
  var numComments = (inputVal != '') ? inputVal : DEFAULT_MAX_COMMENT_NUM;
  var url = "/data?numComment=" + numComments;
  fetch(url).then(response => response.text()).then((quote) => {
    document.getElementById('comment-container').innerText = quote;
  });
}

function deleteAllComments() {
  const params = new URLSearchParams();
  fetch("/delete-data", { method: "POST", body: params });
}

/**
 * The function is to fetch a Promise from "/userLogIn" page, convert the logIn status message
 * into text and add them into the fetch-container displayed on the webpage.
 */
function getUserLogInStatus() {
  fetch("/userLogIn")
    .then((response) =>response.json())
    .then((loginfo) => {
      if (loginfo.status == 1) {
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
          ['Korea', 50]
        ]);

  const options = {
    'title': 'Countries I have been to',
    'width':500,
    'height':400
  };

  const chart = new google.visualization.GeoChart(
      document.getElementById('chart-container'));
  chart.draw(data,options);
}
