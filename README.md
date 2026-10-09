### SQL Data Generator

This is my Spring Boot JDBC project for practice and for helping me
with filling SQL databases with some dummy data.

Project features:
- Implemented to insert into PostgreSQL database;
- JSON file as a full data generation request;
- Simple CLI to be able to preview generated data and run a few execute requests per one session;
- Data is randomly generated withing ranges and patters defined in the request;
- Special generator pattern syntax to generate text values;
- Foreign Key columns data is generated based on the response of the SELECT request to the database;

Not yet implemented:
- Unique columns generation

### How to use
You can run application via IDE or .jar package with `java -jar <package_name>.jar`.

To execute the request file, you can run `generate <absolute_file_path>` or
move it into the working directory and run `generate <filename>`

Then you will get CLI opened and can further follow its suggestions