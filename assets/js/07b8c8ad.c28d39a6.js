"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[580],{3905:(e,t,n)=>{n.d(t,{Zo:()=>s,kt:()=>m});var i=n(7294);function r(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function a(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);t&&(i=i.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,i)}return n}function o(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?a(Object(n),!0).forEach((function(t){r(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):a(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function c(e,t){if(null==e)return{};var n,i,r=function(e,t){if(null==e)return{};var n,i,r={},a=Object.keys(e);for(i=0;i<a.length;i++)n=a[i],t.indexOf(n)>=0||(r[n]=e[n]);return r}(e,t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(i=0;i<a.length;i++)n=a[i],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(r[n]=e[n])}return r}var l=i.createContext({}),p=function(e){var t=i.useContext(l),n=t;return e&&(n="function"==typeof e?e(t):o(o({},t),e)),n},s=function(e){var t=p(e.components);return i.createElement(l.Provider,{value:t},e.children)},d={inlineCode:"code",wrapper:function(e){var t=e.children;return i.createElement(i.Fragment,{},t)}},u=i.forwardRef((function(e,t){var n=e.components,r=e.mdxType,a=e.originalType,l=e.parentName,s=c(e,["components","mdxType","originalType","parentName"]),u=p(n),m=r,f=u["".concat(l,".").concat(m)]||u[m]||d[m]||a;return n?i.createElement(f,o(o({ref:t},s),{},{components:n})):i.createElement(f,o({ref:t},s))}));function m(e,t){var n=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var a=n.length,o=new Array(a);o[0]=u;var c={};for(var l in t)hasOwnProperty.call(t,l)&&(c[l]=t[l]);c.originalType=e,c.mdxType="string"==typeof e?e:r,o[1]=c;for(var p=2;p<a;p++)o[p]=n[p];return i.createElement.apply(null,o)}return i.createElement.apply(null,n)}u.displayName="MDXCreateElement"},3006:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>l,contentTitle:()=>o,default:()=>d,frontMatter:()=>a,metadata:()=>c,toc:()=>p});var i=n(7462),r=(n(7294),n(3905));const a={id:"metric_definition",title:"Metric Definition",sidebar_position:2},o=void 0,c={unversionedId:"api/metric_definition",id:"api/metric_definition",title:"Metric Definition",description:"Metrics are measures of quantitative assessment commonly used for assessing, comparing, and tracking usage or performance of a service. They are the main indicators.",source:"@site/docs/api/metric_definition.md",sourceDirName:"api",slug:"/api/metric_definition",permalink:"/argo-accounting/docs/api/metric_definition",draft:!1,tags:[],version:"current",sidebarPosition:2,frontMatter:{id:"metric_definition",title:"Metric Definition",sidebar_position:2},sidebar:"tutorialSidebar",previous:{title:"Client",permalink:"/argo-accounting/docs/api/client"},next:{title:"Project",permalink:"/argo-accounting/docs/api/project"}},l={},p=[{value:"POST - Create a Metric Definition",id:"post---create-a-metric-definition",level:3},{value:"GET - Fetch a Metric Definition",id:"get---fetch-a-metric-definition",level:3},{value:"PATCH - Update a Metric Definition",id:"patch---update-a-metric-definition",level:3},{value:"DELETE - Delete a Metric Definition",id:"delete---delete-a-metric-definition",level:3},{value:"POST - Search for Metric Definitions",id:"post---search-for-metric-definitions",level:3},{value:"Example 1:",id:"example-1",level:4},{value:"Example 2:",id:"example-2",level:4},{value:"Errors",id:"errors",level:3}],s={toc:p};function d(e){let{components:t,...n}=e;return(0,r.kt)("wrapper",(0,i.Z)({},s,n,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("p",null,"Metrics are measures of quantitative assessment commonly used for assessing, comparing, and tracking usage or performance of a service. They are the main indicators."),(0,r.kt)("p",null,"A metric definition is the way to represent and describe the type of the metrics.  A Metric Definition consists of the metadata describing a Metric. The client can interact through several operations with the API in order to create, update, delete or fetch a Metric Definition. The aforementioned operations are described below."),(0,r.kt)("p",null,"The Metric Definition can be expressed by the following structure:"),(0,r.kt)("table",null,(0,r.kt)("thead",{parentName:"table"},(0,r.kt)("tr",{parentName:"thead"},(0,r.kt)("th",{parentName:"tr",align:null},"Field"),(0,r.kt)("th",{parentName:"tr",align:null},"Description"))),(0,r.kt)("tbody",{parentName:"table"},(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"id"),(0,r.kt)("td",{parentName:"tr",align:null},"Metric Definition unique id")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"metric_name"),(0,r.kt)("td",{parentName:"tr",align:null},"Metric Name to be used for presentation")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"metric_description"),(0,r.kt)("td",{parentName:"tr",align:null},"Short Description of how the metric is collected")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"unit_type"),(0,r.kt)("td",{parentName:"tr",align:null},"Predefined List of Unit Types")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"metric_type"),(0,r.kt)("td",{parentName:"tr",align:null},"Predefined List of Metric Types")))),(0,r.kt)("h3",{id:"post---create-a-metric-definition"},"[POST]"," - Create a Metric Definition"),(0,r.kt)("p",null,"Upon creating a new Metric Definition, the client should be able to forward a Metric to the Accounting System API.\nThe client can submit a new Metric Definition by executing the following POST request:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},'POST /accounting-system/metric-definitions\n\nContent-type: application/json\nAuthorization: Bearer {token}\n\n{\n"metric_name" : "number_of_users",\n"metric_description" : "Number of users",\n"unit_type" : "#",\n"metric_type" : "aggregated"\n}\n')),(0,r.kt)("p",null,"Upon inserting the record into the database, the API returns the Metric Definition enhanced with the metric_definition_id :"),(0,r.kt)("p",null,"Success Response ",(0,r.kt)("inlineCode",{parentName:"p"},"201 CREATED")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},'{\n  "metric_definition_id": "61dc142f6a278e43e8d6b3be",\n  "metric_name" : "number_of_users",\n  "metric_description" : "Number of users",\n  "unit_type" : "#",\n  "metric_type" : "aggregated"\n}\n')),(0,r.kt)("h3",{id:"get---fetch-a-metric-definition"},"[GET]"," - Fetch a Metric Definition"),(0,r.kt)("p",null,"The client should be able to fetch an already created Metric Definition. Having the id of a Metric Definition, the client can request the relevant to that id Metric Definition by executing the following request:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},"GET /accounting-system/metric-definitions/{metric_definition_id}\n\nAuthorization: Bearer {token}\n")),(0,r.kt)("p",null,"The response returned to the client is the following:"),(0,r.kt)("p",null,"Success Response ",(0,r.kt)("inlineCode",{parentName:"p"},"200 OK")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},'{\n  "id" : "61dff744ba5b5f60791bd09d",\n  "metric_name" : "number_of_users",\n  "metric_description" : "Number of users",\n  "unit_type" : "#",\n  "metric_type" : "aggregated"\n}\n')),(0,r.kt)("p",null,"In order to get all records, the following request should be executed:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},"GET /accounting-system/metric-definitions\n\nAuthorization: Bearer {token}\n")),(0,r.kt)("h3",{id:"patch---update-a-metric-definition"},"[PATCH]"," - Update a Metric Definition"),(0,r.kt)("p",null,"The client can update a Metric Definition using the following PATCH request. The URL should be filled in with the metric definition id to be updated."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},"PATCH /accounting-system/metric-definitions/{metric_definition_id}\n\nContent-type: application/json\nAuthorization: Bearer {token}\n\n{\n  attributes_to_be_updated\n}\n")),(0,r.kt)("p",null,"The response will be the updated entity :"),(0,r.kt)("p",null,"Success Response ",(0,r.kt)("inlineCode",{parentName:"p"},"200 OK")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},'{\n   "updated_entity"\n}\n')),(0,r.kt)("p",null,"In order to update the resource properties, the body of the request must contain an updated representation of Metric Definition. For example, to edit the metric type, send the following request:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},'PATCH /accounting-system/metric-definitions/{metric_definition_id}\n\nContent-type: application/json\nAuthorization: Bearer {token}\n\n{  \n"metric_type": "metric_type_to_be_updated"\n}\n')),(0,r.kt)("p",null,"You can update a part or all attributes of the Metric Definition. The empty or null values are ignored."),(0,r.kt)("h3",{id:"delete---delete-a-metric-definition"},"[DELETE]"," - Delete a Metric Definition"),(0,r.kt)("p",null,"You can only delete a Metric Definition that does not have any Metrics assigned to it. If the Metric Definition has no Metrics, you can safely delete it."),(0,r.kt)("p",null,"Metric Definition can be deleted by executing the following request:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},"DELETE /accounting-system/metric-definitions/{metric-definition-id}\n\nAuthorization: Bearer {token}\n")),(0,r.kt)("p",null,"If the operation is successful, you get the following response:"),(0,r.kt)("p",null,"Success Response ",(0,r.kt)("inlineCode",{parentName:"p"},"200 OK")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},'{\n   "code": 200,\n   "message": "The Metric Definition has been deleted successfully."\n}\n')),(0,r.kt)("h3",{id:"post---search-for-metric-definitions"},"[POST]"," - Search for Metric Definitions"),(0,r.kt)("p",null,"You can search on Installations, to find the ones corresponding to the given search criteria. Metric Definitions can be searched by executing the following request:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},"POST accounting-system/metric-definition/search\nContent-Type: application/json\n")),(0,r.kt)("h4",{id:"example-1"},"Example 1:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},'{\n  "type": "query",\n  "field": "metric_type",\n  "values": "count",\n  "operand": "eq"\n}\n')),(0,r.kt)("h4",{id:"example-2"},"Example 2:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},'{\n  "type": "filter",\n  "operator": "OR",\n  "criteria": [\n    {\n      "type": "query",\n      "field": "metric_name",\n      "values": "mdname1",\n      "operand": "eq"\n    },\n    {\n      "type": "filter",\n      "operator": "AND",\n      "criteria": [\n        {\n          "type": "query",\n          "field": "metric_type",\n          "values": "count",\n          "operand": "eq"\n        },\n        {\n          "type": "query",\n          "field": "unit_type",\n          "values": "#",\n          "operand": "eq"\n        }\n      ]\n    }\n    \n')),(0,r.kt)("p",null,"The context of the request should be a json object. The syntax of the json object , is described ",(0,r.kt)("b",null," ",(0,r.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/guides/search_filter"},"here")),"\nIf the operation is successful, you get a list of metrics, for example:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},'{\n    "size_of_page": 1,\n    "number_of_page": 1,\n    "total_elements": 1,\n    "total_pages": 1,\n    "content": [\n        {\n            "metric_definition_id": "6360c7ad3b4ae429c92409dd",\n            "metric_name": "cpu",\n            "metric_description": "CPU metric definition",\n            "unit_type": "#",\n            "metric_type": "aggregated"\n        }\n    ],\n    "links": []\n}\n')),(0,r.kt)("h3",{id:"errors"},"Errors"),(0,r.kt)("p",null,"Please refer to section ",(0,r.kt)("a",{parentName:"p",href:"./api_errors"},"Errors")," to see all possible Errors."))}d.isMDXComponent=!0}}]);