# --- !Ups

INSERT INTO project
(
    project_id,
    name,
    description,
    timestamp_created,
    timestamp_edited,
    billable,
    owned_by,
    created_by,
    last_edited_by,
    client_id
)
VALUES
(
    'a1eda1a6-a749-4932-9f48-16fe5b6a8ce9',
    'Toinen projekti',
    'Testi-projektin kuvaus',
    '2021-01-31 13:21:00',
    '2021-01-31 13:21:00',
    TRUE,
    '9fa407f4-7375-446b-92c6-c578839b7780',
    '06be4b85-8f65-4f65-8965-faba1216f199',
    '06be4b85-8f65-4f65-8965-faba1216f199',
    '5be59e8a-63f5-4f22-8b12-c7128fb40add'
),
(
    'd7e738a2-60cf-4336-b7d9-5216ef960e3a',
    'Projekti Kolmonen',
    'Kolmanen-projektin kuvaus',
    '2021-01-30 13:21:00',
    '2021-01-31 13:21:00',
    TRUE,
    '9fa407f4-7375-446b-92c6-c578839b7780',
    '06be4b85-8f65-4f65-8965-faba1216f199',
    '06be4b85-8f65-4f65-8965-faba1216f199',
    '19f0abb2-fd4f-4db5-b5d6-1549a24c291f'
);


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
    '4276164d-d8c3-47d5-8f65-a6255ce71567',
    'tterava',
    'Teemu',
    'Terävä',
    FALSE,
    '2014-12-13 12:34:55',
    '2015-05-16 13:44:23'
),
(
    '618d79bd-5afd-4f82-b265-729eb7b7e9f0',
    'ttaitava',
    'Taina',
    'Taitava',
    FALSE,
    '2011-03-04 15:23:27',
    '2016-02-01 08:12:41'
),
(
    'c7af4280-7cc0-45aa-84ad-15156a17663e',
    'e_eteva',
    'Eppu',
    'Etevä',
    FALSE,
    '2012-03-04 15:23:27',
    '2018-02-01 08:12:41'
)
;

INSERT INTO project_app_user (project_id, app_user_id)
VALUES
('a1eda1a6-a749-4932-9f48-16fe5b6a8ce9', '4276164d-d8c3-47d5-8f65-a6255ce71567'),
('a1eda1a6-a749-4932-9f48-16fe5b6a8ce9', '618d79bd-5afd-4f82-b265-729eb7b7e9f0'),
('d7e738a2-60cf-4336-b7d9-5216ef960e3a', 'c7af4280-7cc0-45aa-84ad-15156a17663e')
;

INSERT INTO timeinput
(
    timeinput_id,
    app_user_id,
    project_id,
    input_date,
    minutes,
    description,
    timestamp_created,
    timestamp_edited
)
VALUES
(
    '6ee13b35-9c45-4f26-8e44-55599306d2d3',
    '4276164d-d8c3-47d5-8f65-a6255ce71567',
    'a1eda1a6-a749-4932-9f48-16fe5b6a8ce9',
    '2020-02-01',
    480,
    'Projekti alkoi',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    '99145374-219f-4a7d-b564-48f4d0759fac',
    '4276164d-d8c3-47d5-8f65-a6255ce71567',
    'a1eda1a6-a749-4932-9f48-16fe5b6a8ce9',
    '2020-02-02',
    480,
    'Määrittelyä',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'b685dc04-269e-4ec1-92ed-4cdc8012c33f',
    '4276164d-d8c3-47d5-8f65-a6255ce71567',
    'a1eda1a6-a749-4932-9f48-16fe5b6a8ce9',
    '2020-02-03',
    480,
    'Koodausta',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
 (
     'e8b74537-a0c2-4ab7-a21f-bdb0ec190a3a',
     '4276164d-d8c3-47d5-8f65-a6255ce71567',
     'a1eda1a6-a749-4932-9f48-16fe5b6a8ce9',
     '2020-02-04',
     480,
     'Koodausta vielä, vähän testejä',
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP
),
(
    'dc99085e-3511-48bf-a232-4c495192e988',
    '4276164d-d8c3-47d5-8f65-a6255ce71567',
    'a1eda1a6-a749-4932-9f48-16fe5b6a8ce9',
    '2020-02-05',
    480,
    'Homma valmis, deploy ja esittely',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    '00b74127-23c0-4382-965e-8fe4b249809d',
    '618d79bd-5afd-4f82-b265-729eb7b7e9f0',
    'a1eda1a6-a749-4932-9f48-16fe5b6a8ce9',
    '2020-02-01',
    480,
    'Projekti alkoi',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    '250d8d82-aa12-42b8-be26-b80e42fc57e6',
    '618d79bd-5afd-4f82-b265-729eb7b7e9f0',
    'a1eda1a6-a749-4932-9f48-16fe5b6a8ce9',
    '2020-02-02',
    480,
    'Määrittelyä ja testien suunnittelua',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'f9abd021-aa9c-4902-a299-8e3875764ca3',
    '618d79bd-5afd-4f82-b265-729eb7b7e9f0',
    'a1eda1a6-a749-4932-9f48-16fe5b6a8ce9',
    '2020-02-03',
    480,
    'Testisetupin kuntoonlaitto, testejä',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
 (
     '293b553d-c1cc-483b-9285-b0e3de4e1beb',
     '618d79bd-5afd-4f82-b265-729eb7b7e9f0',
     'a1eda1a6-a749-4932-9f48-16fe5b6a8ce9',
     '2020-02-04',
     480,
     'Testausta, testiraportti',
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP
),
(
    '318b6a9e-6a08-4f77-853c-9e119d01aeae',
    '618d79bd-5afd-4f82-b265-729eb7b7e9f0',
    'a1eda1a6-a749-4932-9f48-16fe5b6a8ce9',
    '2020-02-05',
    480,
    'Tulosten esittely',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'b15c7760-bfff-4879-9950-9d023d60efeb',
    'c7af4280-7cc0-45aa-84ad-15156a17663e',
    'd7e738a2-60cf-4336-b7d9-5216ef960e3a',
    '2021-03-05',
    120,
    'Testi-datan lisäämistä',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)

;



# --- !Downs

DELETE FROM timeinput where timeinput_id IN (
'6ee13b35-9c45-4f26-8e44-55599306d2d3',
'99145374-219f-4a7d-b564-48f4d0759fac',
'b685dc04-269e-4ec1-92ed-4cdc8012c33f',
'e8b74537-a0c2-4ab7-a21f-bdb0ec190a3a',
'dc99085e-3511-48bf-a232-4c495192e988',
'00b74127-23c0-4382-965e-8fe4b249809d',
'250d8d82-aa12-42b8-be26-b80e42fc57e6',
'f9abd021-aa9c-4902-a299-8e3875764ca3',
'293b553d-c1cc-483b-9285-b0e3de4e1beb',
'318b6a9e-6a08-4f77-853c-9e119d01aeae',
'b15c7760-bfff-4879-9950-9d023d60efeb'
);

DELETE FROM project_app_user WHERE project_id = 'a1eda1a6-a749-4932-9f48-16fe5b6a8ce9' AND
app_user_id IN ('4276164d-d8c3-47d5-8f65-a6255ce71567', '618d79bd-5afd-4f82-b265-729eb7b7e9f0');

DELETE FROM project_app_user WHERE project_id = 'd7e738a2-60cf-4336-b7d9-5216ef960e3a' AND
        app_user_id = 'c7af4280-7cc0-45aa-84ad-15156a17663e';

DELETE FROM app_user WHERE app_user_id IN ('4276164d-d8c3-47d5-8f65-a6255ce71567',
                                           '618d79bd-5afd-4f82-b265-729eb7b7e9f0',
                                           'c7af4280-7cc0-45aa-84ad-15156a17663e'
                                          );

DELETE FROM project WHERE project_id IN ('a1eda1a6-a749-4932-9f48-16fe5b6a8ce9',
                                         'd7e738a2-60cf-4336-b7d9-5216ef960e3a');
