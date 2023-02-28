---
id: search-filter
title: Syntax a search filter
sidebar_position: 7
---
### Syntax a search filter


You can apply a search operation on the Projects,Providers,Installations, Metrics,Metric Definitions,existing on the Accounting Service.You can search on a specific field or on a combination of fields of the collection. In order to apply a search request,you need to provide the search criteria on the fields,and the search operation will retrieve the results that match them

Defining search criteria, can be done with 2 ways:  
**"query"** : defines a search on a single field of the Collection. e.g on Collection Metrics : installation="insta1"
**"filter"**: defines a search on multiple criteria combined with operators AND/OR. A "filter" contains a combination of "query" or "filter", depending on how complex is the criterio we want to apply. e.g on Collection Metrics: 
a) project="project1" AND installation="insta1" 
b) project="project1" AND (installation="insta1" OR installation="insta2")
c) project="project2" OR [project="project1" AND (installation="insta1" OR installation="insta2")]

### 1.  "query" syntax, searching a single field
The simplest search that can be performed is on a field of the collection.You need to syntax a search "query", in a json format,and provide it as body in the search request. 
The fields that need to be defined,in order the syntax to be valid are: 

| Field          	| Description   	                   | 
|------------------	|---------------------------------------- |
| type             	| The type of the search. In the case the search is a combination of fields of the collection,it’s value is ‘query’ |
| field         	| The field of the collection on which we search
| values         	| The value of the equation, and it can be of any type depending on the type of the field we search
| operand      	| The equation we want to apply on the field in order to search results.it’s value can be : <br/> eq: ==, neq: !=, lt: < , lte: <= , gt: >, gte: >= 


```
{
  "type":string,
  "field": string ,
  "values":primitive,
  "operand": string
}
```

#### Example 1: Search on a specific field, of the collection
You search for Metrics,that the start period is after 01-01-2022.The syntax of the query should be : 

```json

{
  "type":"query",
  "field": "time_period_start" ,
  "values":"2022-01-01T00:00:00Z",
  "operand": "gt"  
}

```


### 2.  "filter" syntax, search on multiple fields

A more complex search can be performed, combining 2 or more fields of the collection. Each time you apply an AND/OR operation on the fields of the Collection, you need to syntax a "filter". The filter can contain a combination of "query", or a combination of "filter", or a combination of "query"/"filter",depending on how complex is the search you apply.
You need to syntax a search "filter",in a json format, and provide it as body in the search request. 
The fields that need to be defined,in order the syntax to be valid are: 

| Field          	| Description   	                   | 
|------------------	|---------------------------------------- |
| type             	| The type of the search. In the case the search is on 2 or more fields of the collection,it’s value is ‘filter’ |
| operator         	|  The operation on which the elements in the criteria will be combined. It’s values is either AND or OR 
| criteria         	| The specific subqueries that will be matched by the operator. Criteria is an array of objects of either ‘query’ or ‘filter’ type



The syntax should be as:

```
{
  "type":string,
  "operator": string ,
  "criteria":array of ‘query’ or ‘filter’ elements
}

```

#### Example 2: Search on combinations of 2 fields, of the collection
You search for Metrics,that the start period is after 01-01-2022 AND the end period is before 01-02-2022.The syntax of the filter should be: 

At first 2 subqueries should be created to define the criterio on each field. 

Query on start period:

```json

{
  "type":"query",
  "field": "time_period_start" ,
  "values":"2022-01-01T00:00:00Z",
  "operand": "gt"  
}

```
Query on end period: 

```json

{
  "type":"query",
  "field": "time_period_end" ,
  "values":"2022-01-02T00:00:00Z",
  "operand": "lt"  
}

```

Now these two queries should be combined in a filter as: 

```json

{
  "type": "filter",
  "operator": "AND",
  "criteria": [
    {
      "type": "query",
      "field": "time_period_start",
      "values": "2022-01-01T00:00:00Z",
      "operand": "gt"
    },
    {
      "type": "query",
      "field": "time_period_end",
      "values": "2022-01-02T00:00:00Z",
      "operand": "lt"
    }
  ]
}

```

#### Example 3: Search on multiple fields, of the collection
You search for Metrics,that start period is after 01-01-2022 and the end period is before 01-02-2022,OR the value is greater than 1000.The syntax of the filter should be as: 

The filter to search for start period is after 01-01-2022 and the end period is before 01-02-2022,if defined as in the Example 2.
The query to search for value greater than 1000 is defined as :

```json
{
  "type":"query",
  "field": "value" ,
  "values":"1000.0",
  "operand": "gt"  
}
```

So now we need to combine these search criteria in a filter as: 

```json

{
  "type": "filter",
  "operator": "OR",
  "criteria": [
    {
      "type": "query",
      "field": "value",
      "values": "1000.0",
      "operand": "gt"
    },
    {
      "type": "filter",
      "operator": "AND",
      "criteria": [
        {
          "type": "query",
          "field": "time_period_start",
          "values": "2022-01-01T00:00:00Z",
          "operand": "gt"
        },
        {
          "type": "query",
          "field": "time_period_end",
          "values": "2022-01-02T00:00:00Z",
          "operand": "lt"
        }
      ]
    }
  ]
}

```
