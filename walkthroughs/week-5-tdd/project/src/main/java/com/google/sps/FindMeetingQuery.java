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
        Collections.singletonList(
            TimeRange.fromStartEnd(TimeRange.START_OF_DAY, TimeRange.END_OF_DAY, true)
        )
    );
    ArrayList<TimeRange> optionalTime = new ArrayList<>(
        Collections.singletonList(
            TimeRange.fromStartEnd(TimeRange.START_OF_DAY, TimeRange.END_OF_DAY, true)
        )
    );
    for (Event event : events) {
      TimeRange eventTime = event.getWhen();
      if (!Collections.disjoint(event.getAttendees(), requiredAttendees)) {
        requiredTime = splitToTwoRange(eventTime, requiredDuration, requiredTime);
      }
      if (!Collections.disjoint(event.getAttendees(), optionalAttendees)) {
        optionalTime = splitToTwoRange(eventTime, requiredDuration, optionalTime);
      }
    }

    // Combine the required and optional time slots to find the intersection
    ArrayList<TimeRange> result = getTimeIntersection(requiredTime, optionalTime, requiredDuration);
    return (result.isEmpty() && !requiredAttendees.isEmpty()) ? requiredTime : result;
  }

  /**
   * * Split an existing time slot list into two parts to avoid occupied time period.
   * @param occupied unaccessible time slots
   * @param duration minimum duration of each time slot
   * @param remainingTime existing list of available slots
   * @return an list of modified available time slots without overlapping with occupied period
   */
  private ArrayList<TimeRange> splitToTwoRange(
      TimeRange occupied, long duration, ArrayList<TimeRange> remainingTime) {
    // Sort the existing time slot list
    Collections.sort(remainingTime, TimeRange.ORDER_BY_START);

    // Find the overlapping time slots and split them based on required duration
    int i = 0;
    while (i < remainingTime.size()) {
      if (remainingTime.get(i).overlaps(occupied)) {
        TimeRange prev = TimeRange.fromStartEnd(
            remainingTime.get(i).start(), occupied.start(), false);
        TimeRange after = TimeRange.fromStartEnd(
            occupied.end(), remainingTime.get(i).end(), false);
        remainingTime.remove(i);
        i -= 1;
        if (prev.duration() >= duration) {
          i += 1;
          remainingTime.add(i, prev);
        }
        if (after.duration() >= duration) {
          i += 1;
          remainingTime.add(i, after);
        }
      }
      i += 1;
    }
    return remainingTime;
  }

  /**
   * Get the intersection of two list of time slots.
   * @param options1 first available slot list
   * @param options2 second available slot list
   * @param requiredDuration required duration for minimum time period
   * @return intersection of option1 and options2
   */
  private ArrayList<TimeRange> getTimeIntersection(
      ArrayList<TimeRange> options1, ArrayList<TimeRange> options2, long requiredDuration) {
    int i = 0;
    int j = 0;
    int m = options1.size();
    int n = options2.size();
    Collections.sort(options1, TimeRange.ORDER_BY_START);
    Collections.sort(options2, TimeRange.ORDER_BY_START);
    ArrayList<TimeRange> resultTime = new ArrayList<>();
    while ((i < m) && (j < n)) {
      TimeRange slot1 =  options1.get(i);
      TimeRange slot2 =  options2.get(j);
      if (slot1.overlaps(slot2)) {
        int start =  Math.max(slot1.start(),slot2.start());
        int end = Math.min(slot1.end(),slot2.end());
        if (TimeRange.fromStartEnd(start,end,false).duration() >= requiredDuration) {
          resultTime.add(TimeRange.fromStartEnd(start,end,false));
        }
      }
      if (slot1.end() <= slot2.end()) {
        i += 1;
      } else {
        j += 1;
      }
    }
    return resultTime;
  }
}