package com.example.dbmasterspringboot.controller.controlcolumn

import com.example.dbmasterspringboot.data.dto.ColumnDTO
import com.example.dbmasterspringboot.data.dto.ResponseDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.sql.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@RestController
class SelectColumnController(
        @Autowired val jdbcTemplate: JdbcTemplate
) {

    @RequestMapping("/v1/column/get-all")
    fun selectAllData(
            @RequestBody response: HashMap<String, String>
    ): ResponseDTO {
        val name = response["name"]
        val tableName = response["tableName"]

        if (name == null || tableName == null) {
            return ResponseDTO("E01", "파라미터가 잘못 설정됬습니다. tableName, name", "")
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

            val SELECT_ALL_QUERY = "SELECT * FROM $name.$tableName;"
            var pstmt = con.prepareStatement(SELECT_ALL_QUERY)
            var mapArray : ArrayList<ColumnDTO> = ArrayList()

            try {
                println("current $tableName is:")
                con.prepareStatement(
                        SELECT_ALL_QUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).use { selectStmt ->
                    selectStmt.executeQuery().use { rs ->
                        if (!rs.isBeforeFirst()) {
                            println("no rows found")
                        } else {
                            while (rs.next()) {
                                for (i in 1 until rs.getMetaData().getColumnCount() + 1) {
                                    val columnDTO = ColumnDTO(rs.metaData.getColumnName(i),rs.getObject(i))
                                    mapArray.add(columnDTO)
//                                    print(" {" + rs.getMetaData().getColumnName(i) + ":" + rs.getObject(i)+ ",}")
                                }
//                                println("")
                            }
                        }
                    }
                }
            } catch (e: SQLException) {
                throw RuntimeException(e)
            }
            pstmt.close()
            con.close()
            println("Disconnected From DB ..........")
            return ResponseDTO("S01", "", mapArray)

        } catch (e: SQLException) {
            System.err.print("SQLException : " + e.message)
            return ResponseDTO("E01", e.toString(), "")
        }
    }


}