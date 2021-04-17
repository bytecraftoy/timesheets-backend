# --- !Ups

INSERT INTO app_user
(
    app_user_id,
    username,
    first_name,
    last_name,
    is_manager,
    timestamp_created,
    timestamp_edited
)
VALUES
(
    'f2c93281-bb5f-43dc-83ae-6b3f1d8816e0',
    'tteekkari',
    'Teemu',
    'Teekkari',
    FALSE,
    '2003-01-09 18:53:34',
    '2006-07-02 12:20:14'
);

UPDATE project_app_user SET app_user_id = 'f2c93281-bb5f-43dc-83ae-6b3f1d8816e0' WHERE project_id = 'a3eb6db5-5212-46d0-bd08-8e852a45e0d3' AND app_user_id = '9fa407f4-7375-446b-92c6-c578839b7780';

# --- !Downs

DELETE FROM project_app_user WHERE project_id = 'a3eb6db5-5212-46d0-bd08-8e852a45e0d3' AND app_user_id = 'f2c93281-bb5f-43dc-83ae-6b3f1d8816e0';

DELETE FROM app_user WHERE app_user_id = 'f2c93281-bb5f-43dc-83ae-6b3f1d8816e0';

INSERT INTO project_app_user (project_id, app_user_id)
VALUES ('a3eb6db5-5212-46d0-bd08-8e852a45e0d3', '9fa407f4-7375-446b-92c6-c578839b7780');
