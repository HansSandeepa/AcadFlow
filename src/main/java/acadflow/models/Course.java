package acadflow.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

    public class Course {

        private final SimpleStringProperty courseId;
        private final SimpleStringProperty name;
        private final SimpleIntegerProperty credit;
        private final SimpleStringProperty type;
        private final SimpleStringProperty department;

        public Course(String courseId, String name, int credit, String type, String department) {
            this.courseId = new SimpleStringProperty(courseId);
            this.name = new SimpleStringProperty(name);
            this.credit = new SimpleIntegerProperty(credit);
            this.type = new SimpleStringProperty(type);
            this.department = new SimpleStringProperty(department);
        }

        // Getters
        public String getCourseId()
        {

            return courseId.get();
        }

        public String getName() {

            return name.get();
        }

        public int getCredit() {
            return credit.get();
        }

        public String getType() {
            return type.get();
        }

        public String getDepartment() {
            return department.get();
        }


        public SimpleStringProperty courseIdProperty() {
            return courseId;
        }

        public SimpleStringProperty nameProperty() {
            return name;
        }

        public SimpleIntegerProperty creditProperty() {
            return credit;
        }
        public SimpleStringProperty typeProperty() {
            return type;
        }
        public SimpleStringProperty departmentProperty() {
            return department;
        }
    }

