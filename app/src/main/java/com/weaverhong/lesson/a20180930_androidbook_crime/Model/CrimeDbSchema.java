package com.weaverhong.lesson.a20180930_androidbook_crime.Model;

// 该类的作用是，描述CrimeTable的表名、各列的列名
// schema意为计划、图表
public class CrimeDbSchema {
    public static final class CrimeTable {
        public static final String NAME = "crimes";

        // 注意，在这里更新后，所有用到SQL“新增”语句的地方要注意修改
        // 比如Helper、CrimeLab等
        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
        }
        // 注意，在这里更新后，所有用到SQL“新增”语句的地方要注意修改
    }
}
