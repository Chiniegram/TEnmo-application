TRUNCATE users, accounts, transfers CASCADE;

INSERT INTO users (user_id, username, password_hash)
VALUES (1001, 'test', 'x1111'),
       (1002, 'test2', 'x1111x');

INSERT INTO accounts (account_id, user_id, balance)
VALUES (2001, 1001, 1000.00),
       (2002, 1002, 1000.00);

INSERT INTO transfers (transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount)
VALUES (5001, 2, 2, 2001, 2002, 50),
       (5002, 2, 2, 2002, 2001, 200);



