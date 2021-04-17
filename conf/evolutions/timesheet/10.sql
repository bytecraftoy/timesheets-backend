# --- !Ups

INSERT INTO project (project_id, name, description, timestamp_created, timestamp_edited, billable, owned_by, created_by, last_edited_by, client_id)
VALUES ('d9be4f9a-029c-43ed-9a4e-d9379fa86a00', 'Owner only for Matt D.', 'No employees added', '2021-04-03 13:02:00', '2021-04-03 13:02:00', TRUE, 'b36c6232-dec4-4600-ad71-5018f58cd0fd', 'b36c6232-dec4-4600-ad71-5018f58cd0fd', 'b36c6232-dec4-4600-ad71-5018f58cd0fd', '1bb44a7e-cd7c-447d-a9e9-26495b52fa88')
;

INSERT INTO timeinput (timeinput_id, app_user_id, project_id, input_date, minutes, description, timestamp_created, timestamp_edited)
  VALUES ('d62ecf64-9b6a-4748-aec8-2c10ffb0772c', 'b36c6232-dec4-4600-ad71-5018f58cd0fd', 'd9be4f9a-029c-43ed-9a4e-d9379fa86a00', '2021-04-01', 300, 'Matt´s April´s Fools acting', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
,        ('d62ecf64-9b6a-4748-aec8-2c10ffb0772d', 'b36c6232-dec4-4600-ad71-5018f58cd0fd', 'd9be4f9a-029c-43ed-9a4e-d9379fa86a00', '2021-04-06', 90, 'Personal trainer', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
,        ('d62ecf64-9b6a-4748-aec8-2c10ffb0772e', 'b36c6232-dec4-4600-ad71-5018f58cd0fd', 'd9be4f9a-029c-43ed-9a4e-d9379fa86a00', '2021-04-07', 60, 'Studio meeting', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
;

# --- !Downs

DELETE FROM timeinput WHERE timeinput_id IN
('d62ecf64-9b6a-4748-aec8-2c10ffb0772c',
 'd62ecf64-9b6a-4748-aec8-2c10ffb0772d',
 'd62ecf64-9b6a-4748-aec8-2c10ffb0772e');

DELETE FROM project WHERE project_id IN ('d9be4f9a-029c-43ed-9a4e-d9379fa86a00');
