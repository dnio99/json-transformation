# JsonTransformation

[![Minimum Scala Version](https://img.shields.io/badge/scala-%3E%3D%202.13-8892BF.svg)](https://www.scala-lang.org/)

Json转换，封装[jsonata](https://jsonata.org/)，采用graalvm集成js模块。


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