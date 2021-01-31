# --- !Ups

INSERT INTO project_app_user (project_id, app_user_id) VALUES ('a3eb6db5-5212-46d0-bd08-8e852a45e0d3', '06be4b85-8f65-4f65-8965-faba1216f199');


INSERT INTO timeinput (timeinput_id,app_user_id,project_id,input_date,minutes,timestamp_created,timestamp_edited)
	VALUES ('10f55cb4-8cfa-442f-9a98-e630a37ce4a1','06be4b85-8f65-4f65-8965-faba1216f199','a3eb6db5-5212-46d0-bd08-8e852a45e0d3','2021-01-20',480,'2021-01-30 08:18:50.917','2021-01-30 08:18:50.917');
INSERT INTO timeinput (timeinput_id,app_user_id,project_id,input_date,minutes,description,timestamp_created,timestamp_edited)
	VALUES ('10f55cb4-8cfa-442f-9a98-e630a37ce4a2','06be4b85-8f65-4f65-8965-faba1216f199','a3eb6db5-5212-46d0-bd08-8e852a45e0d3','2021-01-21',480,'torstai toivoa täynnä','2021-01-30 08:18:50.918','2021-01-30 08:18:50.918');
INSERT INTO timeinput (timeinput_id,app_user_id,project_id,input_date,minutes,description,timestamp_created,timestamp_edited)
	VALUES ('955b499d-c16f-4bb2-9f53-644c9b38b8c3','06be4b85-8f65-4f65-8965-faba1216f199','a3eb6db5-5212-46d0-bd08-8e852a45e0d3','2021-01-22',480,'TGIF','2021-01-30 08:18:50.920','2021-01-30 08:18:50.920');
INSERT INTO timeinput (timeinput_id,app_user_id,project_id,input_date,minutes,description,timestamp_created,timestamp_edited)
	VALUES ('2f3c36cf-1cc4-4c94-86f4-69eaa9fc4b64','06be4b85-8f65-4f65-8965-faba1216f199','a3eb6db5-5212-46d0-bd08-8e852a45e0d3','2021-01-25',60,'','2021-01-30 08:20:44.203','2021-01-30 08:20:44.203');
INSERT INTO timeinput (timeinput_id,app_user_id,project_id,input_date,minutes,description,timestamp_created,timestamp_edited)
	VALUES ('41d93246-7f42-4be5-9779-6e55b0bbf2f5','06be4b85-8f65-4f65-8965-faba1216f199','a3eb6db5-5212-46d0-bd08-8e852a45e0d3','2021-01-26',60,'','2021-01-30 08:21:00.939','2021-01-30 08:21:00.939');
INSERT INTO timeinput (timeinput_id,app_user_id,project_id,input_date,minutes,description,timestamp_created,timestamp_edited)
	VALUES ('65205173-2019-41e2-bacc-88bbd913d5a6','06be4b85-8f65-4f65-8965-faba1216f199','a3eb6db5-5212-46d0-bd08-8e852a45e0d3','2021-01-18',60,'testi','2021-01-30 08:18:50.914','2021-01-30 08:28:50.639');
INSERT INTO timeinput (timeinput_id,app_user_id,project_id,input_date,minutes,description,timestamp_created,timestamp_edited)
	VALUES ('4946934a-4d81-4b5a-b512-a5c07d46faa7','06be4b85-8f65-4f65-8965-faba1216f199','a3eb6db5-5212-46d0-bd08-8e852a45e0d3','2021-01-19',120,'testi','2021-01-30 08:18:50.916','2021-01-30 08:39:52.350');
INSERT INTO timeinput (timeinput_id,app_user_id,project_id,input_date,minutes,description,timestamp_created,timestamp_edited)
	VALUES ('28d041b0-d523-42ab-9d2f-fd7cbd88f878','06be4b85-8f65-4f65-8965-faba1216f199','a3eb6db5-5212-46d0-bd08-8e852a45e0d3','2021-01-30',500,'feferfe','2021-01-30 08:45:58.901','2021-01-30 08:46:44.568');
INSERT INTO timeinput (timeinput_id,app_user_id,project_id,input_date,minutes,description,timestamp_created,timestamp_edited)
	VALUES ('43d4d5e0-5a46-47ff-961b-2abfbbbff4b9','06be4b85-8f65-4f65-8965-faba1216f199','a3eb6db5-5212-46d0-bd08-8e852a45e0d3','2021-01-31',120,'','2021-01-30 08:51:16.146','2021-01-30 08:53:26.838');


# --- !Downs

DELETE FROM timeinput WHERE timeinput_id in
('10f55cb4-8cfa-442f-9a98-e630a37ce4a1',
'10f55cb4-8cfa-442f-9a98-e630a37ce4a2',
'28d041b0-d523-42ab-9d2f-fd7cbd88f878',
'2f3c36cf-1cc4-4c94-86f4-69eaa9fc4b64',
'41d93246-7f42-4be5-9779-6e55b0bbf2f5',
'43d4d5e0-5a46-47ff-961b-2abfbbbff4b9',
'4946934a-4d81-4b5a-b512-a5c07d46faa7',
'65205173-2019-41e2-bacc-88bbd913d5a6',
'955b499d-c16f-4bb2-9f53-644c9b38b8c3');

DELETE FROM project_app_user WHERE project_id = 'a3eb6db5-5212-46d0-bd08-8e852a45e0d3' AND app_user_id = '06be4b85-8f65-4f65-8965-faba1216f199';
