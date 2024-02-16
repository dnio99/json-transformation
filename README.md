# JsonTransformation

[![Minimum Scala Version](https://img.shields.io/badge/scala-%3E%3D%202.13-8892BF.svg)](https://www.scala-lang.org/)

Json转换，封装[jsonata](https://jsonata.org/)，采用graalvm集成js模块。

## Json转换

请求示例：
```shell
curl --location 'http://localhost:6600/v1/transformations' \
--header 'accept: application/json' \
--header 'Content-Type: application/json' \
--data '{
    "rawData": {
        "Account": {
            "Account Name": "Firefly",
            "Order": [
                {
                    "OrderID": "order103",
                    "Product": [
                        {
                            "Product Name": "Bowler Hat",
                            "ProductID": 858383,
                            "SKU": "0406654608",
                            "Description": {
                                "Colour": "Purple",
                                "Width": 300,
                                "Height": 200,
                                "Depth": 210,
                                "Weight": 0.75
                            },
                            "Price": 34.45,
                            "Quantity": 2
                        },
                        {
                            "Product Name": "Trilby hat",
                            "ProductID": 858236,
                            "SKU": "0406634348",
                            "Description": {
                                "Colour": "Orange",
                                "Width": 300,
                                "Height": 200,
                                "Depth": 210,
                                "Weight": 0.6
                            },
                            "Price": 21.67,
                            "Quantity": 1
                        }
                    ]
                },
                {
                    "OrderID": "order104",
                    "Product": [
                        {
                            "Product Name": "Bowler Hat",
                            "ProductID": 858383,
                            "SKU": "040657863",
                            "Description": {
                                "Colour": "Purple",
                                "Width": 300,
                                "Height": 200,
                                "Depth": 210,
                                "Weight": 0.75
                            },
                            "Price": 34.45,
                            "Quantity": 4
                        },
                        {
                            "ProductID": 345664,
                            "SKU": "0406654603",
                            "Product Name": "Cloak",
                            "Description": {
                                "Colour": "Black",
                                "Width": 30,
                                "Height": 20,
                                "Depth": 210,
                                "Weight": 2
                            },
                            "Price": 107.99,
                            "Quantity": 1
                        }
                    ]
                }
            ]
        }
    },
    "expression": "{\"dd\": $sum(Account.Order.Product.(Price * Quantity))}"
}'
```

返回结果：
```json
{"dd":336.36}
```

## csv To Json

csv格式须知：
- 第一行是无意义，可以用于中文名字等
- 第二行是jsonata语法，可以用于对数据格式化等转换
- 第三行是json path路径，也就是json具体格式的定义

```shell
curl --location 'http://localhost:6600/v1/transformations/csv' \
--header 'accept: application/octet-stream' \
--header 'Content-Type: application/octet-stream' \
--data '@test.csv'
```
返回结果
```json
[
    {
        "humanInfo": {
            "name": "John",
            "name1": "Doe"
        },
        "test1": "120 jefferson st.",
        "contact": {
            "address": "Riverside"
        },
        "test2": " NJ",
        "numbers": [
            8075
        ]
    },
    {
        "humanInfo": {
            "name": "Jack",
            "name1": "McGinnis"
        },
        "test1": "220 hobo Av.",
        "contact": {
            "address": "Phila"
        },
        "test2": " PA",
        "numbers": [
            9119
        ]
    },
    {
        "humanInfo": {
            "name": "John \"Da Man\"",
            "name1": "Repici"
        },
        "test1": "120 Jefferson St.",
        "contact": {
            "address": "Riverside"
        },
        "test2": " NJ",
        "numbers": [
            8075
        ]
    },
    {
        "humanInfo": {
            "name": "Stephen",
            "name1": "Tyler"
        },
        "test1": "7452 Terrace \"At the Plaza\" road",
        "contact": {
            "address": "SomeTown"
        },
        "test2": "SD",
        "numbers": [
            91234
        ]
    },
    {
        "humanInfo": {
            "name": "",
            "name1": "Blankman"
        },
        "test1": "",
        "contact": {
            "address": "SomeTown"
        },
        "test2": " SD",
        "numbers": [
            298
        ]
    },
    {
        "humanInfo": {
            "name": "\"Joan \"\"the bone\"\"",
            "name1": " Anne\""
        },
        "test1": "Jet",
        "contact": {
            "address": "\"9th"
        },
        "test2": " at Terrace plc\"",
        "numbers": [
            null
        ]
    }
]
```