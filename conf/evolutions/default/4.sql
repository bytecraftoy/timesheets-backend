# --- !Ups

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
    '65205173-2019-41e2-bacc-88bbd913d5a7',
    '9fa407f4-7375-446b-92c6-c578839b7780',
    'a3eb6db5-5212-46d0-bd08-8e852a45e0d3',
    '2020-01-18',
    480,
    'tein hommia',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

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
    '4946934a-4d81-4b5a-b512-a5c07d46faa1',
    '9fa407f4-7375-446b-92c6-c578839b7780',
    'a3eb6db5-5212-46d0-bd08-8e852a45e0d3',
    '2020-01-19',
    240,
    'lyhyt päivä',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

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
    '0c247911-2f4f-4a7b-8972-6b528dadb1cc',
    '9fa407f4-7375-446b-92c6-c578839b7780',
    'a3eb6db5-5212-46d0-bd08-8e852a45e0d3',
    '2020-01-20',
    480,
    NULL,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

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
    '10f55cb4-8cfa-442f-9a98-e630a37ce4ab',
    '9fa407f4-7375-446b-92c6-c578839b7780',
    'a3eb6db5-5212-46d0-bd08-8e852a45e0d3',
    '2020-01-21',
    480,
    'torstai toivoa täynnä',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

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
    '955b499d-c16f-4bb2-9f53-644c9b38b8cc',
    '9fa407f4-7375-446b-92c6-c578839b7780',
    'a3eb6db5-5212-46d0-bd08-8e852a45e0d3',
    '2020-01-22',
    480,
    'TGIF',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);


# --- !Downs

DELETE FROM timeinput WHERE timeinput_id in
                            (
                             '65205173-2019-41e2-bacc-88bbd913d5a7',
                             '4946934a-4d81-4b5a-b512-a5c07d46faa1',
                             '0c247911-2f4f-4a7b-8972-6b528dadb1cc',
                             '10f55cb4-8cfa-442f-9a98-e630a37ce4ab',
                             '955b499d-c16f-4bb2-9f53-644c9b38b8cc'
                            );
