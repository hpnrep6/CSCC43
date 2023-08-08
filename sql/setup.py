import sys
import mysql.connector

db = mysql.connector.connect(
    host="localhost",
    user="root",
    password=""
)

def exec(script):
    cursor = db.cursor()
    cursor.execute(script)
    cursor.close()
    return cursor

def getFileLines(fileName):
    table_file = open(fileName)
    table_scripts = table_file.read()
    table_file.close()
    return table_scripts.split('\n\n')

    
DROP_SCRIPT = \
'''
DROP DATABASE IF EXISTS MyBnB;
'''

INIT_SCRIPTS = getFileLines('tables.sql')

DATA_SCRIPTS = []

DATA_SCRIPTS.append(getFileLines('data/countries.sql'))
DATA_SCRIPTS.append(getFileLines('data/listingtypes.sql'))
DATA_SCRIPTS.append(getFileLines('data/data.sql'))


match sys.argv[1]:
    case 'clean':
        with db.cursor() as cursor:
            cursor.execute(DROP_SCRIPT)
        print('Dropped database.')
    case 'setup':
        exec(DROP_SCRIPT)
        for script in INIT_SCRIPTS:
            print(script)
            exec(script)
            print('\n\n')
        for script_collection in DATA_SCRIPTS:
            for script in script_collection:
                print(script)
                exec(script)
                print('\n\n')
        db.commit()
        db.close()
        print('Initilised database.')
            
    case _:
        print('Select `clean` or `setup`.')