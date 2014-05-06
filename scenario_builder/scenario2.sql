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



