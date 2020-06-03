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

/**
 * Adds a random description to the page.
 */
function addRandomDescription() {
  const descriptions =
      ['Noogler!', 'Dog Girl', 'Programmer', 'Food Lover'];

  // Pick a random description.
  const description = descriptions[Math.floor(Math.random() * descriptions.length)];

  // Add it to the page.
  const descriptionContainer = document.getElementById('description-container');
  descriptionContainer.innerText = description;
}


/**
 * The function is to fetch a Promise from "/data" page, convert the response message 
 * into text and add them into the fetch-container displayed on the webpage.    
 */
function getHellofromFetchedPractice() {
  fetch('/data').then(response => response.text()).then((quote) => {
    document.getElementById('fetch-container').innerText = quote;
  });
}


/**
 * The function is to fetch the comments from "/data" page, convert the comment message 
 * into text and add them into the comment containter displayed on the webpage.    
 */
function getCommentInForm() {
  fetch('/data').then(response => response.text()).then((comment) => {
    document.getElementById('comment-container').innerText = comment;
  });
}
