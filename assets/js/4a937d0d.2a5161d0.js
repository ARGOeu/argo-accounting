"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[890],{3905:(e,t,n)=>{n.d(t,{Zo:()=>p,kt:()=>g});var a=n(7294);function r(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);t&&(a=a.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,a)}return n}function l(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){r(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function o(e,t){if(null==e)return{};var n,a,r=function(e,t){if(null==e)return{};var n,a,r={},i=Object.keys(e);for(a=0;a<i.length;a++)n=i[a],t.indexOf(n)>=0||(r[n]=e[n]);return r}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(a=0;a<i.length;a++)n=i[a],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(r[n]=e[n])}return r}var s=a.createContext({}),c=function(e){var t=a.useContext(s),n=t;return e&&(n="function"==typeof e?e(t):l(l({},t),e)),n},p=function(e){var t=c(e.components);return a.createElement(s.Provider,{value:t},e.children)},u={inlineCode:"code",wrapper:function(e){var t=e.children;return a.createElement(a.Fragment,{},t)}},d=a.forwardRef((function(e,t){var n=e.components,r=e.mdxType,i=e.originalType,s=e.parentName,p=o(e,["components","mdxType","originalType","parentName"]),d=c(n),g=r,f=d["".concat(s,".").concat(g)]||d[g]||u[g]||i;return n?a.createElement(f,l(l({ref:t},p),{},{components:n})):a.createElement(f,l({ref:t},p))}));function g(e,t){var n=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var i=n.length,l=new Array(i);l[0]=d;var o={};for(var s in t)hasOwnProperty.call(t,s)&&(o[s]=t[s]);o.originalType=e,o.mdxType="string"==typeof e?e:r,l[1]=o;for(var c=2;c<i;c++)l[c]=n[c];return a.createElement.apply(null,l)}return a.createElement.apply(null,n)}d.displayName="MDXCreateElement"},6137:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>s,contentTitle:()=>l,default:()=>u,frontMatter:()=>i,metadata:()=>o,toc:()=>c});var a=n(7462),r=(n(7294),n(3905));const i={id:"installation",title:"Installation",sidebar_position:5},l=void 0,o={unversionedId:"api/installation",id:"api/installation",title:"Installation",description:"We use the term installation as it is defined in the Virtual Access documentation to refer to a specific instance or part of a resource/service that is allocated to a specific Project by one Provider.",source:"@site/docs/api/installation.md",sourceDirName:"api",slug:"/api/installation",permalink:"/argo-accounting/docs/api/installation",draft:!1,tags:[],version:"current",sidebarPosition:5,frontMatter:{id:"installation",title:"Installation",sidebar_position:5},sidebar:"tutorialSidebar",previous:{title:"Provider",permalink:"/argo-accounting/docs/api/provider"},next:{title:"Metric",permalink:"/argo-accounting/docs/api/metric"}},s={},c=[{value:"POST - Create a new Installation",id:"post---create-a-new-installation",level:3},{value:"DELETE - Delete an existing Installation",id:"delete---delete-an-existing-installation",level:3},{value:"PATCH - Update an existing Installation",id:"patch---update-an-existing-installation",level:3},{value:"GET - Fetch an existing Installation",id:"get---fetch-an-existing-installation",level:3},{value:"GET Fetch all Project Installations",id:"get-fetch-all-project-installations",level:3},{value:"GET Fetch all Provider Installations",id:"get-fetch-all-provider-installations",level:3},{value:"POST - Access Control Entry for a particular Installation",id:"post---access-control-entry-for-a-particular-installation",level:3},{value:"POST - Search for Installations",id:"post---search-for-installations",level:3},{value:"Example 1:",id:"example-1",level:4},{value:"Example 2:",id:"example-2",level:4},{value:"Errors",id:"errors",level:3}],p={toc:c};function u(e){let{components:t,...n}=e;return(0,r.kt)("wrapper",(0,a.Z)({},p,n,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("p",null,"We use the term installation as it is defined in the Virtual Access documentation to refer to a specific instance or part of a resource/service that is allocated to a specific Project by one Provider."),(0,r.kt)("p",null,"An Installation can only be generated through the endpoint we are going to describe below."),(0,r.kt)("p",null,"The Installation collection has the following structure:"),(0,r.kt)("table",null,(0,r.kt)("thead",{parentName:"table"},(0,r.kt)("tr",{parentName:"thead"},(0,r.kt)("th",{parentName:"tr",align:null},"Field"),(0,r.kt)("th",{parentName:"tr",align:null},"Description"))),(0,r.kt)("tbody",{parentName:"table"},(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"project"),(0,r.kt)("td",{parentName:"tr",align:null},"It must point to a Project ID that has already been registered")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"organisation"),(0,r.kt)("td",{parentName:"tr",align:null},"It must point to a Provider ID that has been either registered through the EOSC Resource Catalogue or Accounting System API")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"infrastructure"),(0,r.kt)("td",{parentName:"tr",align:null},"Short name of infrastructure")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"installation"),(0,r.kt)("td",{parentName:"tr",align:null},"Short name of installation")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"unit_of_access"),(0,r.kt)("td",{parentName:"tr",align:null},"It must point to an existing Metric Definition. Obviously, you can add different Metrics to an Installation, but this attribute expresses the primary Unit of Access")))),(0,r.kt)("h3",{id:"post---create-a-new-installation"},"[POST]"," - Create a new Installation"),(0,r.kt)("p",null,"You can submit a new Installation by executing the following POST request:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},'POST /accounting-system/installations\n\nContent-type: application/json\nAuthorization: Bearer {token}\n\n{\n  "project" : "101017567t",\n  "organisation" : "grnet",\n  "infrastructure" : "okeanos-knossos",\n  "installation" : "GRNET-KNS",\n  "unit_of_access" : "62973fea0f41a20c683e9014"\n}\n')),(0,r.kt)("p",null,"Upon inserting the record into the database, the API returns the Installation enhanced with the generated installation ID :"),(0,r.kt)("p",null,"Success Response ",(0,r.kt)("inlineCode",{parentName:"p"},"201 CREATED")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},'{\n  "id" : "61dc142f6a278e43e8d6b3be",\n  "project" : "101017567t",\n  "organisation" : "grnet",\n  "infrastructure" : "okeanos-knossos",\n  "installation" : "GRNET-KNS",\n  "unit_of_access": {\n        "metric_definition_id": "62973fea0f41a20c683e9014",\n        "metric_name": "lalala",\n        "metric_description": "Number of users",\n        "unit_type": "#",\n        "metric_type": "aggregated"\n    }\n}\n')),(0,r.kt)("h3",{id:"delete---delete-an-existing-installation"},"[DELETE]"," - Delete an existing Installation"),(0,r.kt)("p",null,"You can also delete an existing Installation by executing the following request:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},"DELETE /accounting-system/installations/{installation_id}\n\nAuthorization: Bearer {token}\n")),(0,r.kt)("p",null,"If the deletion is successful the following response is returned:"),(0,r.kt)("p",null,"Success Response ",(0,r.kt)("inlineCode",{parentName:"p"},"200 OK")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},'{\n   "code": 200,\n   "message": "Installation has been deleted successfully."\n}\n')),(0,r.kt)("h3",{id:"patch---update-an-existing-installation"},"[PATCH]"," - Update an existing Installation"),(0,r.kt)("p",null,"You can update an existing Installation by executing the following request:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},'PATCH /accounting-system/installations/{installation_id}\n\nContent-type: application/json\nAuthorization: Bearer {token}\n\n{\n  "organisation" : "organisation to be updated",\n  "infrastructure" : "infrastructure to be updated",\n  "installation" : "installation to be updated",\n  "unit_of_access" : "unit_of_access to be updated"\n}\n')),(0,r.kt)("p",null,"The body of the request must contain an updated representation of Installation. You can update a part or all attributes of the Installation. The empty or null values are ignored."),(0,r.kt)("p",null,"The response will be the updated entity :"),(0,r.kt)("p",null,"Success Response ",(0,r.kt)("inlineCode",{parentName:"p"},"200 OK")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},'{\n   "updated_entity"\n}\n')),(0,r.kt)("h3",{id:"get---fetch-an-existing-installation"},"[GET]"," - Fetch an existing Installation"),(0,r.kt)("p",null,"You can fetch a created Installation by executing the following GET HTTP request:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},"GET /accounting-system/installations/{installation_id}\n\nAuthorization: Bearer {token}\n")),(0,r.kt)("p",null,"The response is as follows:"),(0,r.kt)("p",null,"Success Response ",(0,r.kt)("inlineCode",{parentName:"p"},"200 OK")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},'{\n    "id": "6350f13072dda00a3ce5f0cb",\n    "project": "725025",\n    "organisation": "sites",\n    "infrastructure": "infra-grnet-test",\n    "installation": "installation-grnet",\n    "unit_of_access": {\n        "metric_definition_id": "6350f12772dda00a3ce5f0ca",\n        "metric_name": "lalala",\n        "metric_description": "Number of users",\n        "unit_type": "#",\n        "metric_type": "aggregated"\n    }\n}\n')),(0,r.kt)("h3",{id:"get-fetch-all-project-installations"},"[GET]"," Fetch all Project Installations"),(0,r.kt)("p",null,"Essentially, the following endpoint returns all Installations available in a specific Project. By default, the first page of 10 Installations will be returned."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},"GET /accounting-system/projects/{project_id}/installations\n\nAuthorization: Bearer {token}\n")),(0,r.kt)("p",null,"You can tune the default values by using the query parameters page and size as shown in the example below."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},"GET /accounting-system/projects/{project_id}/installations?page=2&size=15\n\nAuthorization: Bearer {token}\n")),(0,r.kt)("p",null,"The above request returns the second page which contains 15 Installations:"),(0,r.kt)("p",null,"Success Response 200 OK"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},'{\n   "size_of_page": 15,\n   "number_of_page": 2,\n   "total_elements": 237,\n   "total_pages": 16,\n   "content": [\n   {\n   "id": "62986c61683f693f470bb67c",\n   "organisation": "grnet",\n   "infrastructure": "okeanos-knossos",\n   "installation": "GRNET-KNS",\n   "unit_of_access": {\n       "metric_definition_id": "62986c4e683f693f470bb67b",\n       "metric_name": "number_of_users",\n       "metric_description": "Number of users",\n       "unit_type": "#",\n       "metric_type": "aggregated"\n   }\n   }\n   ],\n   "links": [\n       {\n           "href": "https://acc.devel.argo.grnet.gr/accounting-system/installations?page=1&size=15",\n           "rel": "first"\n       },\n       {\n           "href": "https://acc.devel.argo.grnet.gr/accounting-system/installations?page=16&size=15",\n           "rel": "last"\n       },\n       {\n           "href": "https://acc.devel.argo.grnet.gr/accounting-system/installations?page=2&size=15",\n           "rel": "self"\n       },\n       {\n           "href": "https://acc.devel.argo.grnet.gr/accounting-system/installations?page=1&size=15",\n           "rel": "prev"\n       },\n       {\n           "href": "https://acc.devel.argo.grnet.gr/accounting-system/installations?page=3&size=15",\n           "rel": "next"\n       }\n   ]\n}\n')),(0,r.kt)("h3",{id:"get-fetch-all-provider-installations"},"[GET]"," Fetch all Provider Installations"),(0,r.kt)("p",null,"Essentially, the following endpoint returns all Installations available in a Provider belonging to a specific Project. By default, the first page of 10 Installations will be returned."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},"GET /accounting-system/projects/{project_id}/providers/{provider_id}/installations\n\nAuthorization: Bearer {token}\n")),(0,r.kt)("p",null,"You can tune the default values by using the query parameters page and size as shown in the example below."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},"GET /accounting-system/projects/{project_id}/providers/{provider_id}/installations?page=2&size=15\n\nAuthorization: Bearer {token}\n")),(0,r.kt)("p",null,"The above request returns the second page which contains 15 Installations:"),(0,r.kt)("p",null,"Success Response 200 OK"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},'{\n   "size_of_page": 15,\n   "number_of_page": 2,\n   "total_elements": 237,\n   "total_pages": 16,\n   "content": [\n        {\n            "id": "6350f13072dda00a3ce5f0cb",\n            "project": "725025",\n            "organisation": "sites",\n            "infrastructure": "infra-grnet-test",\n            "installation": "installation-grnet",\n            "unit_of_access": {\n                "metric_definition_id": "6350f12772dda00a3ce5f0ca",\n                "metric_name": "lalala",\n                "metric_description": "Number of users",\n                "unit_type": "#",\n                "metric_type": "aggregated"\n            }\n        }\n    ],\n   "links": [\n       {\n           "href": "https://acc.devel.argo.grnet.gr/accounting-system/installations?page=1&size=15",\n           "rel": "first"\n       },\n       {\n           "href": "https://acc.devel.argo.grnet.gr/accounting-system/installations?page=16&size=15",\n           "rel": "last"\n       },\n       {\n           "href": "https://acc.devel.argo.grnet.gr/accounting-system/installations?page=2&size=15",\n           "rel": "self"\n       },\n       {\n           "href": "https://acc.devel.argo.grnet.gr/accounting-system/installations?page=1&size=15",\n           "rel": "prev"\n       },\n       {\n           "href": "https://acc.devel.argo.grnet.gr/accounting-system/installations?page=3&size=15",\n           "rel": "next"\n       }\n   ]\n}\n')),(0,r.kt)("h3",{id:"post---access-control-entry-for-a-particular-installation"},"[POST]"," - Access Control Entry for a particular Installation"),(0,r.kt)("p",null,"The same goes for the Installations. Any client can have different responsibilities at different Installations. The actions the client can perform at each Installation are determined by the role, and the permissions it has."),(0,r.kt)("p",null,"To grant a role to a client on a specific Installation, you have to execute the following request:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},'POST /accounting-system/installations/{installation_id}/acl/{who}\n\nContent-Type: application/json\nAuthorization: Bearer {token}\n \n{\n  "roles":[\n     {role_name}\n  ]\n}\n')),(0,r.kt)("p",null,"where {who} is the client ID in which the roles will be assigned."),(0,r.kt)("p",null,"The response is :"),(0,r.kt)("p",null,"Success Response ",(0,r.kt)("inlineCode",{parentName:"p"},"200 OK")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},'{\n   "code": 200,\n   "message": "Installation Access Control was successfully created."\n}\n')),(0,r.kt)("p",null,(0,r.kt)("strong",{parentName:"p"},"Keep in mind that")," to execute the above operation, you must have been assigned a role containing the Installation Acl permission."),(0,r.kt)("h3",{id:"post---search-for-installations"},"[POST]"," - Search for Installations"),(0,r.kt)("p",null,"You can search on Installations, to find the ones corresponding to the given search criteria. Installations  can be searched by executing the following request:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},"POST accounting-system/installations/search\nContent-Type: application/json\n")),(0,r.kt)("h4",{id:"example-1"},"Example 1:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},'{\n           "type":"query",\n           "field": "installation",\n           "values": "GRNET-KNS-1",\n           "operand": "eq"         \n\n}\n')),(0,r.kt)("h4",{id:"example-2"},"Example 2:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},'{\n "type": "filter",\n "operator": "OR",\n "criteria": [\n   {\n     "type": "filter",\n     "operator": "OR",\n     "criteria": [{\n           "type":"query",\n           "field": "installation",\n           "values": "GRNET-KNS-1",\n           "operand": "eq"         \n\n},{\n           "type":"query",\n           "field": "organisation",\n           "values": "grnet",\n           "operand": "eq"         \n\n}]\n\n   }]}\n')),(0,r.kt)("p",null,"The context of the request can be a json object of type \u2018query\u2019 or \u2018filter\u2019.\n\u2018query\u2019 defines a criterion in a specific field of the installation. "),(0,r.kt)("p",null,"\u2018query\u2019 can be expressed as a json object :"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},'{\n  "type":string,\n  "field": string ,\n  "values":primitive,\n  "operand": string  \n}\n')),(0,r.kt)("p",null,"In the \u2018query\u2019 element we need to define the following properties: "),(0,r.kt)("table",null,(0,r.kt)("thead",{parentName:"table"},(0,r.kt)("tr",{parentName:"thead"},(0,r.kt)("th",{parentName:"tr",align:null},"Field"),(0,r.kt)("th",{parentName:"tr",align:null},"Description"))),(0,r.kt)("tbody",{parentName:"table"},(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"type"),(0,r.kt)("td",{parentName:"tr",align:null},"The type of the search and it\u2019s value is \u2018query\u2019")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"field"),(0,r.kt)("td",{parentName:"tr",align:null},"The field of the collection on which we search")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"values"),(0,r.kt)("td",{parentName:"tr",align:null},"The value of the equation , and it can be of any type depending on the type of the field we search")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"operand"),(0,r.kt)("td",{parentName:"tr",align:null},"The equation we want to apply on the field in order to search results. it\u2019s value can be {eq, neq, lt, lte, gt, gte}")))),(0,r.kt)("p",null,(0,r.kt)("strong",{parentName:"p"},"Example 1"),' defines a search on field title. The \u2018query\u2019 searches for installations  that have installation="GRNET-KNS-1" or organisation="grnet"\n\u2018filter\u2019 defines multiple criteria and the way they are combined . A filter can include criteria of \u2018filter\u2019 or \u2018query\u2019 types.\n\u2018filter\u2019 can be expressed as a json object :'),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},'{\n  "type":string,\n  "operator": string ,\n  criteria:array of \u2018query\u2019 or \u2018filter\u2019 elements\n}\n')),(0,r.kt)("p",null,"In the \u2018query\u2019 element we need to define the following properties: "),(0,r.kt)("table",null,(0,r.kt)("thead",{parentName:"table"},(0,r.kt)("tr",{parentName:"thead"},(0,r.kt)("th",{parentName:"tr",align:null},"Field"),(0,r.kt)("th",{parentName:"tr",align:null},"Description"))),(0,r.kt)("tbody",{parentName:"table"},(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"type"),(0,r.kt)("td",{parentName:"tr",align:null},"The type of the search and it\u2019s value is \u2018filter\u2019")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"operator"),(0,r.kt)("td",{parentName:"tr",align:null},"The operation on which the elements in the criteria will be combined. it\u2019s values is AND or OR")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"criteria"),(0,r.kt)("td",{parentName:"tr",align:null},"The specific subqueries that will be matched by the operator. criteria is an array of objects of \u2018query\u2019 or \u2018filter\u2019 type")))),(0,r.kt)("p",null,(0,r.kt)("strong",{parentName:"p"},"Example 2"),' defines a \u2018filter\u2019 containing criteria both of filter and query type. The \u2018filter\u2019 searches for  installations   that have installation="GRNET-KNS-1"  OR organisation=\u2019grnet\u2019'),(0,r.kt)("p",null,"If the operation is successful, you get a list of installations"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},'{\n   "size_of_page": 2,\n   "number_of_page": 1,\n   "total_elements": 2,\n   "total_pages": 1,\n   "content": [\n       {\n           "installation_id": "62de52a3be6b3a161e01c75b",\n           "project": "750802",\n           "organisation": "sites",\n           "infrastructure": "okeanos-knossos-1",\n           "installation": "GRNET-KNS-1",\n           "unit_of_access": {\n               "metric_definition_id": "62de528dbe6b3a161e01c75a",\n               "metric_name": "number_of_active_users",\n               "metric_description": "Number of active users",\n               "unit_type": "#",\n               "metric_type": "aggregated"\n           }\n       },\n       {\n           "installation_id": "62de532cbe6b3a161e01c75d",\n           "project": "750802",\n           "organisation": "grnet",\n           "infrastructure": "okeanos-knossos-2",\n           "installation": "GRNET-KNS-2",\n           "unit_of_access": {\n               "metric_definition_id": "62de531cbe6b3a161e01c75c",\n               "metric_name": "number_of_users_deleted",\n               "metric_description": "Number of deleted users",\n               "unit_type": "#",\n               "metric_type": "aggregated"\n           }\n       }\n   ],\n   "links": []\n}\n')),(0,r.kt)("h3",{id:"errors"},"Errors"),(0,r.kt)("p",null,"Please refer to section ",(0,r.kt)("a",{parentName:"p",href:"./api_errors"},"Errors")," to see all possible Errors."))}u.isMDXComponent=!0}}]);