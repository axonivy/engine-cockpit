-- create system application

INSERT INTO IWA_SecurityDescriptor VALUES (1, 1);
INSERT INTO IWA_SecurityDescriptor VALUES (2, 1);

INSERT INTO IWA_SecuritySystem VALUES (0, 'system', 1);
INSERT INTO IWA_SecuritySystem VALUES (1, 'default', 2);

INSERT INTO IWA_Application VALUES (0, 'system', 'System Application', 'System', 2, '', 0);

INSERT INTO IWA_SecurityMember VALUES ('ROLE-82A0ED62-A290-48E1-84B3-8410439F7C1B', 'Everybody', 'Everybody', 'Everybody', 0, 1, 0);
INSERT INTO IWA_Role VALUES (0, 'Everybody', 'Everybody', 'Top level role', NULL, 0, 0, 'ROLE-82A0ED62-A290-48E1-84B3-8410439F7C1B');

INSERT INTO IWA_SecurityMember VALUES ('ROLE-E55E082D-E7AF-4757-A837-E91F4A8DD540', 'SystemAdministrator', 'SystemAdministrator', 'System Administrator', 0, 1, 0);
INSERT INTO IWA_Role VALUES (1, 'SystemAdministrator', 'System Administrator', 'Technical ivy system role', 'ROLE-82A0ED62-A290-48E1-84B3-8410439F7C1B', 0, 0, 'ROLE-E55E082D-E7AF-4757-A837-E91F4A8DD540');
