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
    'b36c6232-dec4-4600-ad71-5018f58cd0fd',
    'md',
    'Matt',
    'Damon',
    TRUE,
    '2004-10-19 10:23:54',
    '2004-10-19 10:23:54'
),
(
    '9ed66e85-f5b6-4c29-966d-15c229e4aef5',
    'bc',
    'Billy',
    'Crystal',
    TRUE,
    '2004-10-20 11:30:45',
    '2012-11-19 16:27:01'
),
(
    '3759d225-5acd-4e36-b3dc-58c5d7a6b61d',
    'ct',
    'Chrissy',
    'Teigen',
    TRUE,
    '2004-10-19 10:23:54',
    '2004-10-19 10:23:54'
),
(
    'acc1f6da-e5ac-42a9-b9be-da72c0c410be',
    'eg',
    'Eva',
    'Green',
    TRUE,
    '2004-10-19 10:23:54',
    '2004-10-19 10:23:54'
),
(
    'da57eeef-fc92-4338-8ace-472c40acae45',
    'jimmyf',
    'Jimmy',
    'Fallon',
    FALSE,
    '2004-10-19 10:23:54',
    '2004-10-19 10:23:54'
),
(
    '81f0fa42-6106-4285-8551-2d81afca36e2',
    'jimmyk',
    'Jimmy',
    'Kimmel',
    FALSE,
    '2004-10-19 10:23:54',
    '2004-10-19 10:23:54'
),
(
    'b38d4dd9-7e27-4dc7-8346-231ff83f4183',
    'sambee',
    'Samantha',
    'Bee',
    FALSE,
    '2004-10-19 10:23:54',
    '2004-10-19 10:23:54'
),
(
    '59739b03-4576-421f-bd8b-a9be865fbe1c',
    'ow',
    'Oprah',
    'Winfrey',
    FALSE,
    '2004-10-19 10:23:54',
    '2004-10-19 10:23:54'
)
;


# --- !Downs
DELETE FROM app_user WHERE username IN ('md','ow', 'sambee','jimmyk', 'jimmyf', 'eg', 'ct');