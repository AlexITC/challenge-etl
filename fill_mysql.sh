#!/bin/bash
set -e

echo "Creating DDL"
mysql -u root -proot --port=33060 --host=127.0.1.1 < data/database-ddl.sql

echo "Importing Person"
mysql -u root -proot --port=33060 --host=127.0.1.1 < data/01-person.sql

echo "Importing Term"
mysql -u root -proot --port=33060 --host=127.0.1.1 < data/02-term.sql

echo "Importing Class"
mysql -u root -proot --port=33060 --host=127.0.1.1 < data/03-class.sql

echo "Importing Building"
mysql -u root -proot --port=33060 --host=127.0.1.1 < data/04-building.sql

echo "Importing Class Term"
mysql -u root -proot --port=33060 --host=127.0.1.1 < data/05-class_term.sql

echo "Importing Term Enrolment"
mysql -u root -proot --port=33060 --host=127.0.1.1 < data/06-term_enrolment.sql

echo "Importing Class Enrolment"
mysql -u root -proot --port=33060 --host=127.0.1.1 < data/07-class_enrolment.sql
