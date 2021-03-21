# --- !Ups

INSERT INTO project (project_id, name, description, timestamp_created, timestamp_edited, billable, owned_by, created_by, last_edited_by, client_id)
  VALUES ('40726707-57d1-47e0-a82b-6d85320b160b', 'Loma', 'Non-billable', '2021-03-21 13:16:00', '2021-03-21 13:16:00', FALSE, '9fa407f4-7375-446b-92c6-c578839b7780', '06be4b85-8f65-4f65-8965-faba1216f199', '06be4b85-8f65-4f65-8965-faba1216f199', '5be59e8a-63f5-4f22-8b12-c7128fb40add');

INSERT INTO project_app_user (project_id, app_user_id) VALUES ('40726707-57d1-47e0-a82b-6d85320b160b', '4276164d-d8c3-47d5-8f65-a6255ce71567');

INSERT INTO timeinput (timeinput_id, app_user_id, project_id, input_date, minutes, description, timestamp_created, timestamp_edited)
  VALUES ('8778a907-448a-4460-aa55-75263f266593', '4276164d-d8c3-47d5-8f65-a6255ce71567', '40726707-57d1-47e0-a82b-6d85320b160b', '2020-02-01', 60, 'Yksi tunti', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


# --- !Downs

DELETE FROM timeinput where timeinput_id IN ('8778a907-448a-4460-aa55-75263f266593');

DELETE FROM project_app_user WHERE project_id = '40726707-57d1-47e0-a82b-6d85320b160b' AND
  app_user_id = ('4276164d-d8c3-47d5-8f65-a6255ce71567');

DELETE FROM project WHERE project_id IN ('40726707-57d1-47e0-a82b-6d85320b160b');
