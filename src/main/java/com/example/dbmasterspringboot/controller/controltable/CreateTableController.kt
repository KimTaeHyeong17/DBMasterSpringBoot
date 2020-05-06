package com.example.dbmasterspringboot.controller.controltable

import com.example.dbmasterspringboot.data.dto.ResponseDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.DatabaseMetaDataCallback
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.sql.*
import java.time.LocalDate
import java.util.ArrayList
import javax.servlet.http.HttpServletRequest


@RestController
class CreateTableController(
        @Autowired val jdbcTemplate: JdbcTemplate
) {

    /* Table 만드는 함수 */
    @RequestMapping("/v1/table/create")
    fun createTable(
            @RequestBody response: HashMap<String, String>
    ): ResponseDTO {
        var resultStr = "failed"
        val currentTime = LocalDate.now()
        val tableName = response["tableName"]
        val fieldInfo = response["fieldInfo"]
        val name = response["name"]

        if (tableName == null || fieldInfo == null || name == null) {
            return ResponseDTO("파라미터가 잘못 설정됬습니다.")
        }
        //fielddInfo = sno int(11) NOT NULL, name char(10) DEFAULT NULL, PRIMARY KEY (sno)

        return try {
            val CREATE_TABLE_QUERY = "CREATE TABLE $name.$tableName ($fieldInfo) default character set utf8 collate utf8_general_ci;"
            println(CREATE_TABLE_QUERY)
            jdbcTemplate.execute(CREATE_TABLE_QUERY)
            ResponseDTO("S01")

        } catch (e: Exception) {
            resultStr = e.cause.toString()
            ResponseDTO(resultStr)
        }
    }

    /* 사용자 데이터베이스 전체 테이블 목록 가져옴. */
    @RequestMapping("/v1/table/all-tables")
    fun selectTable(
            @RequestBody response: HashMap<String, String>
    ): ResponseDTO {
        //파라미터 받고
        val name = response["name"]
        val pw = response["pw"]

        if (name == null) {
            return ResponseDTO("파라미터가 잘못 설정됬습니다. tableName, name")
        }

        //디비 커넥션 준비
        var url: String? = "jdbc:mysql://54.180.95.198:5536/dbmaster_master?serverTimezone=UTC&useSSL=true"
        var con: Connection? = null
        var stmt: Statement? = null
        //디비안의 모든 테이블 디비 이름=name

        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
        } catch (e: ClassNotFoundException) {
            System.err.print("ClassNotFoundException : " + e.message)
        }
        try {
            con = DriverManager.getConnection(url, "dbmaster", "root")
            println("Connected to DB ............")
            stmt = con.createStatement()

            val resultSet = con.metaData.getTables(name,null,"%",null);
            val resultArrayList = ArrayList<String>()
            while (resultSet.next()){
                System.out.println(resultSet.getString(3))
                resultArrayList.add(resultSet.getString(3))
            }
            stmt.close()
            con.close()
            println("Disconnected From DB ..........")
            return ResponseDTO(resultArrayList.toString())

        } catch (e: SQLException) {
            System.err.print("SQLException : " + e.message)
            return ResponseDTO(e.toString())
        }
    }

    @RequestMapping("/v1/table/insert")
    fun insertTable(
            @RequestBody response: HashMap<String, String>
    ): ResponseDTO {
        //파라미터 받고
        val tableName = response["tableName"]
        val name = response["name"]
        val insert = response["insert"]
        if (tableName == null || name == null || insert == null) {
            return ResponseDTO("파라미터가 잘못 설정됬습니다. tableName, name")
        }
        println(tableName)
        println(name)
        println(insert)

        return try {
            val SELECT_ALL_FROM_TABLE = "INSERT INTO $name.$tableName VALUES($insert);"
            println(SELECT_ALL_FROM_TABLE)
            jdbcTemplate.execute(SELECT_ALL_FROM_TABLE)
            ResponseDTO("S01")

        } catch (e: SQLException) {
            System.err.print("SQLException : " + e.message)
            ResponseDTO(e.toString())
        }

    }
}