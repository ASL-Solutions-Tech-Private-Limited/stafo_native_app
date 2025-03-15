package com.stafo.app.base.model

object EmpConstants {
    fun getEmployeeData():ArrayList<Employee>{
        val employeeList=ArrayList<Employee>()
        val emp1=Employee("Chinmaya Mohapatra","Android Developer")
        employeeList.add(emp1)
        val emp2=Employee("Ram prakash","Web Dev")
        employeeList.add(emp2)
        val emp3=Employee("OMM Meheta","React")
        employeeList.add(emp3)
        val emp4=Employee("Hari Mohapatra","Designer")
        employeeList.add(emp4)
        val emp5=Employee("Abhisek Mishra","Software Dev")
        employeeList.add(emp5)
        val emp6=Employee("Sindhu Malhotra","Software Developer")
        employeeList.add(emp6)

        return  employeeList
    }
}