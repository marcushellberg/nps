-- Clean existing test data
TRUNCATE TABLE responses CASCADE;
TRUNCATE TABLE survey_links CASCADE;
TRUNCATE TABLE surveys CASCADE;
TRUNCATE TABLE target_emails CASCADE;
TRUNCATE TABLE target_lists CASCADE;

-- Insert test target lists
INSERT INTO target_lists (id, version, name)
VALUES (1, 0, 'Community'),
       (2, 0, 'Employees');

-- Insert test emails
INSERT INTO target_emails (version, email_address, target_list_id)
VALUES (0, 'john.doe@example.com', 1),
       (0, 'jane.smith@example.com', 1),
       (0, 'bob.wilson@example.com', 1),
       (0, 'emma.davis@company.com', 2),
       (0, 'alex.turner@company.com', 2);

-- Insert test survey
INSERT INTO surveys (id, version, name, question, email_subject, email_body, target_list_id)
VALUES (1, 0, 'Product Feedback',
        'How likely are you to recommend our product to a friend or colleague?',
        'Quick feedback request',
        'Hi there,\n\nWe value your opinion! Please take a moment to answer this quick survey: $URL\n\nBest regards,\nThe Team',
        1);

-- Reset sequences
SELECT setval('target_lists_id_seq', (SELECT MAX(id) FROM target_lists));
SELECT setval('target_emails_id_seq', (SELECT MAX(id) FROM target_emails));
SELECT setval('surveys_id_seq', (SELECT MAX(id) FROM surveys));