# --- !Ups
INSERT INTO client
(
    client_id,
    name,
    email,
    timestamp_created,
    timestamp_edited
)
VALUES
(
    'b50dfea4-7910-4e82-9ec0-c835fdbc9f5a',
    'Gravity Ltd',
    'isaac@gravity.co.uk',
    '2004-10-20 11:30:45',
    '2012-11-19 16:27:01'
),
(
    '091fe75a-f52b-4bde-9f61-0c46011ea283',
    'Relativity GmbH',
    'albert@relativity.de',
    '2014-03-20 09:08:42',
    '2016-06-19 17:39:39'
),
(
    'd15c9196-0bcf-4771-9fdd-247194609b44',
    'Radiation SA',
    'marie@radiation.pl',
    '2018-10-21 10:30:46',
    '2020-10-20 14:21:10'
);

# --- !Downs
DELETE FROM client
WHERE client_id IN ('b50dfea4-7910-4e82-9ec0-c835fdbc9f5a',
                    '091fe75a-f52b-4bde-9f61-0c46011ea283',
                    'd15c9196-0bcf-4771-9fdd-247194609b44');
