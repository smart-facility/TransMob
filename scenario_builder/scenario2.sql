/* This file is part of TransMob.

   TransMob is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   TransMob is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser Public License for more details.

   You should have received a copy of the GNU Lesser Public License
   along with TransMob.  If not, see <http://www.gnu.org/licenses/>.

*/
update life_event.birth_probability set first_child = first_child*0.25;
update life_event.birth_probability set second_child = second_child*0.25;
update life_event.birth_probability set third_child = third_child*0.25;
update life_event.birth_probability set fourth_child = fourth_child*0.25;
update life_event.birth_probability set fifth_child = fifth_child*0.25;
update life_event.birth_probability set six_or_more = six_or_more*0.25;
update life_event.divorce_probability_male set probability=probability*0.25;
update life_event.divorce_probability_female set probability=probability*0.25;
update life_event.marriage_probability_male set probability=probability*4;
update life_event.marriage_probability_female set probability=probability*4;



