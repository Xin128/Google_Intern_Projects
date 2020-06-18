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

package com.google.sps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public final class FindMeetingQuery {
  /**
   * create a query to select available time slots based on request.
   * @param events existing events for attendees to participate
   * @param request request of meetings with specific time and attendees
   * @return a collection of available time slots
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> requiredAttendees = request.getAttendees();
    Collection<String> optionalAttendees = request.getOptionalAttendees();
    long requiredDuration = request.getDuration();

    // Check edge cases when the required duration is too long
    if (requiredDuration > TimeRange.WHOLE_DAY.duration()) {
      return new ArrayList<>();
    }

    // Find the available time slots for required attendees and optional attendees
    ArrayList<TimeRange> requiredTime = new ArrayList<>(
        Collections.singletonList(TimeRange.WHOLE_DAY
        ));
    ArrayList<TimeRange> allConsideredTime = new ArrayList<>(
        Collections.singletonList(TimeRange.WHOLE_DAY
        ));

    for (Event event : events) {
      TimeRange eventTime = event.getWhen();
      if (!Collections.disjoint(event.getAttendees(), requiredAttendees)) {
        reduceAvailability(eventTime, requiredDuration, requiredTime);
        reduceAvailability(eventTime,requiredDuration,allConsideredTime);
      }
      if (!Collections.disjoint(event.getAttendees(), optionalAttendees)) {
        reduceAvailability(eventTime,requiredDuration,allConsideredTime);
      }
    }
    return (allConsideredTime.isEmpty() && !requiredAttendees.isEmpty())
               ? requiredTime : allConsideredTime;
  }

  /**
   * * Split an existing time slot list into two parts to avoid occupied time period.
   * @param occupied unaccessible time slots
   * @param duration minimum duration of each time slot
   * @param remainingTime existing list of available slots
   * @return an list of modified available time slots without overlapping with occupied period
   */
  private void reduceAvailability(
      TimeRange occupied, long duration, ArrayList<TimeRange> remainingTime) {

    // Find the overlapping time slots and split them based on required duration
    int index = 0;
    while (index < remainingTime.size()) {
      if (remainingTime.get(index).overlaps(occupied)) {
        TimeRange prev = TimeRange.fromStartEnd(
            remainingTime.get(index).start(), occupied.start(), false);
        TimeRange after = TimeRange.fromStartEnd(
            occupied.end(), remainingTime.get(index).end(), false);
        remainingTime.remove(index);
        index -= 1;
        if (prev.duration() >= duration) {
          index += 1;
          remainingTime.add(index, prev);
        }
        if (after.duration() >= duration) {
          index += 1;
          remainingTime.add(index, after);
        }
      }
      index += 1;
    }
  }
}