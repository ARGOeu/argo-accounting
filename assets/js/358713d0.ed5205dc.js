"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[7596],{5150:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>o,contentTitle:()=>c,default:()=>l,frontMatter:()=>r,metadata:()=>a,toc:()=>d});var i=t(4848),s=t(8453);const r={id:"unit_type",title:"Unit Type",sidebar_position:8},c=void 0,a={id:"api/unit_type",title:"Unit Type",description:"A Unit Type expresses and measures physical quantities used in various infrastructures, service providers, and projects. It is used to describe a class or group of Units based on a single characteristic.",source:"@site/docs/api/unit_type.md",sourceDirName:"api",slug:"/api/unit_type",permalink:"/argo-accounting/docs/api/unit_type",draft:!1,unlisted:!1,tags:[],version:"current",sidebarPosition:8,frontMatter:{id:"unit_type",title:"Unit Type",sidebar_position:8},sidebar:"tutorialSidebar",previous:{title:"Metric Type",permalink:"/argo-accounting/docs/api/metric_type"},next:{title:"Resource",permalink:"/argo-accounting/docs/api/resource"}},o={},d=[{value:"[POST] - Create a Unit Type",id:"post---create-a-unit-type",level:3},{value:"[GET] - Fetch a Unit Type",id:"get---fetch-a-unit-type",level:3},{value:"[PATCH] - Update a Unit Type",id:"patch---update-a-unit-type",level:3},{value:"[DELETE]  - Delete a Unit Type",id:"delete----delete-a-unit-type",level:3},{value:"[GET]  - Fetch all the Unit Types",id:"get----fetch-all-the-unit-types",level:3},{value:"Errors",id:"errors",level:3}];function p(e){const n={a:"a",code:"code",h3:"h3",p:"p",pre:"pre",table:"table",tbody:"tbody",td:"td",th:"th",thead:"thead",tr:"tr",...(0,s.R)(),...e.components};return(0,i.jsxs)(i.Fragment,{children:[(0,i.jsxs)(n.p,{children:["A Unit Type expresses and measures physical quantities used in various infrastructures, service providers, and projects. It is used to describe a class or group of Units based on a single characteristic.\nOnce a Unit Type is generated, it can be used as an attribute of a ",(0,i.jsx)(n.a,{href:"/argo-accounting/docs/api/metric_definition",children:"Metric Definition"}),"."]}),"\n",(0,i.jsx)(n.p,{children:"The client can interact through several operations with the API in order to create, update, delete or fetch a Unit Type. The aforementioned operations are described below."}),"\n",(0,i.jsx)(n.p,{children:"The Unit Type can be expressed by the following structure:"}),"\n",(0,i.jsxs)(n.table,{children:[(0,i.jsx)(n.thead,{children:(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.th,{children:"Field"}),(0,i.jsx)(n.th,{children:"Description"})]})}),(0,i.jsxs)(n.tbody,{children:[(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.td,{children:"id"}),(0,i.jsx)(n.td,{children:"A unique identifier"})]}),(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.td,{children:"unit_type"}),(0,i.jsx)(n.td,{children:"The Unit Type"})]}),(0,i.jsxs)(n.tr,{children:[(0,i.jsx)(n.td,{children:"description"}),(0,i.jsx)(n.td,{children:"Short Description of a Unit Type"})]})]})]}),"\n",(0,i.jsx)(n.h3,{id:"post---create-a-unit-type",children:"[POST] - Create a Unit Type"}),"\n",(0,i.jsx)(n.p,{children:"The client can submit a new Unit Type by executing the following POST request:"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{children:'POST /accounting-system/unit-types\n\nContent-type: application/json\nAuthorization: Bearer {token}\n\n{\n    "unit_type" : "number_of_users",\n    "description" : "Number of users"\n}\n'})}),"\n",(0,i.jsx)(n.p,{children:"Upon inserting the record into the database, the API returns the stored Unit Type :"}),"\n",(0,i.jsxs)(n.p,{children:["Success Response ",(0,i.jsx)(n.code,{children:"201 CREATED"})]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{children:'{\n    "id": "63cfa31aca73200e5a7707b6",\n    "unit_type": "number_of_users",\n    "description": "Number of users",\n    "creator_id": "115143399384cc3177df5377691ccdbb284cb245fad1c@aai.eosc-portal.eu"\n}\n'})}),"\n",(0,i.jsx)(n.p,{children:"The response also contains the id generated by the database and the creator_id, which is the client's unique ID."}),"\n",(0,i.jsx)(n.h3,{id:"get---fetch-a-unit-type",children:"[GET] - Fetch a Unit Type"}),"\n",(0,i.jsx)(n.p,{children:"The client should be able to fetch an existing Unit Type. By passing the Unit Type ID, the client can request the corresponding Unit Type by executing the following request:"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{children:"GET /accounting-system/unit-types/{unit_type_id}\n\nAuthorization: Bearer {token}\n"})}),"\n",(0,i.jsx)(n.p,{children:"The response returned to the client is the following:"}),"\n",(0,i.jsxs)(n.p,{children:["Success Response ",(0,i.jsx)(n.code,{children:"200 OK"})]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{children:'{\n    "id": "63cfa31aca73200e5a7707b6",\n    "unit_type": "number_of_users",\n    "description": "Number of users",\n    "creator_id": "115143399384cc3177df5377691ccdbb284cb245fad1c@aai.eosc-portal.eu"\n}\n'})}),"\n",(0,i.jsx)(n.h3,{id:"patch---update-a-unit-type",children:"[PATCH] - Update a Unit Type"}),"\n",(0,i.jsx)(n.p,{children:"You can update a part or all attributes of an existing Unit Type using the following PATCH request. The empty or null values are ignored."}),"\n",(0,i.jsx)(n.p,{children:"You cannot update a Unit Type registered by Accounting Service or a Unit Type used in an existing Metric Definition."}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{children:'PATCH /accounting-system/unit-types/{unit_type_id}\n\nContent-type: application/json\nAuthorization: Bearer {token}\n\n\n{\n    "unit_type" : "unit_type_to_be_updated",\n    "description" : "description_to_be_updated"\n}\n'})}),"\n",(0,i.jsx)(n.p,{children:"The response will be the updated entity :"}),"\n",(0,i.jsxs)(n.p,{children:["Success Response ",(0,i.jsx)(n.code,{children:"200 OK"})]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{children:'{\n    "id": "63cfa31aca73200e5a7707b6",\n    "unit_type": "updated_unit_type",\n    "description": "updated_description",\n    "creator_id": "115143399384cc3177df5377691ccdbb284cb245fad1c@aai.eosc-portal.eu"\n}\n'})}),"\n",(0,i.jsx)(n.p,{children:"To update the properties of an actual Unit Type, the body of the request must contain an updated representation of it. For example, to edit the unit type, send the following request:"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{children:'PATCH /accounting-system/unit-types/{unit_type_id}\n\nContent-type: application/json\nAuthorization: Bearer {token}\n\n{  \n    "unit_type": "unit_type_to_be_updated"\n}\n'})}),"\n",(0,i.jsx)(n.h3,{id:"delete----delete-a-unit-type",children:"[DELETE]  - Delete a Unit Type"}),"\n",(0,i.jsx)(n.p,{children:"A Unit Type can be deleted by executing the following request:"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{children:"DELETE /accounting-system/unit-types/{unit_type_id}\n\nAuthorization: Bearer {token}\n"})}),"\n",(0,i.jsx)(n.p,{children:"If the operation is successful, you get the following response:"}),"\n",(0,i.jsxs)(n.p,{children:["Success Response ",(0,i.jsx)(n.code,{children:"200 OK"})]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{children:'{\n    "code": 200,\n    "message": "The Unit Type has been deleted successfully."\n}\n'})}),"\n",(0,i.jsx)(n.p,{children:"You cannot delete a Unit Type registered by Accounting Service or a Unit Type used in an existing Metric Definition."}),"\n",(0,i.jsx)(n.h3,{id:"get----fetch-all-the-unit-types",children:"[GET]  - Fetch all the Unit Types"}),"\n",(0,i.jsx)(n.p,{children:"You can also fetch all the Unit Types that exist to the accounting system."}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{children:"GET /accounting-system/unit-types\n\nAuthorization: Bearer {token}\n"})}),"\n",(0,i.jsx)(n.p,{children:"If the operation is successful, you get the following response:"}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{children:'{\n    "size_of_page": 10,\n    "number_of_page": 1,\n    "total_elements": 11,\n    "total_pages": 2,\n    "content": [\n        {\n            "id": "63cfd515f5c3c21ba78ca3a1",\n            "unit_type": "TB",\n            "description": "terabyte",\n            "creator_id": ""\n        },\n        {\n            "id": "63cfd515f5c3c21ba78ca3a2",\n            "unit_type": "TB/year",\n            "description": "terabyte per year",\n            "creator_id": ""\n        },\n        {\n            "id": "63cfd515f5c3c21ba78ca3a3",\n            "unit_type": "Endpoints Monitored/hour",\n            "description": "Endpoints Monitored per hour",\n            "creator_id": ""\n        },\n        {\n            "id": "63cfd515f5c3c21ba78ca3a4",\n            "unit_type": "Messages/hour",\n            "description": "Messages per hour",\n            "creator_id": ""\n        },\n        {\n            "id": "63cfd515f5c3c21ba78ca3a5",\n            "unit_type": "Service Updates",\n            "description": "Service Updates",\n            "creator_id": ""\n        },\n        {\n            "id": "63cfd515f5c3c21ba78ca3a6",\n            "unit_type": "#",\n            "description": "number of",\n            "creator_id": ""\n        },\n        {\n            "id": "63cfd515f5c3c21ba78ca3a7",\n            "unit_type": "count",\n            "description": "count of",\n            "creator_id": ""\n        },\n        {\n            "id": "63cfd515f5c3c21ba78ca3a8",\n            "unit_type": "API reqs",\n            "description": "API requests",\n            "creator_id": ""\n        },\n        {\n            "id": "63cfd515f5c3c21ba78ca3a9",\n            "unit_type": "PID prefixes",\n            "description": "PID prefixes",\n            "creator_id": ""\n        },\n        {\n            "id": "63cfd515f5c3c21ba78ca3aa",\n            "unit_type": "CPU Time",\n            "description": "the exact amount of time that the CPU has spent processing data",\n            "creator_id": ""\n        }\n    ],\n    "links": [\n        {\n            "href": "http://localhost:8080/accounting-system/unit-types?page=1&size=10",\n            "rel": "first"\n        },\n        {\n            "href": "http://localhost:8080/accounting-system/unit-types?page=2&size=10",\n            "rel": "last"\n        },\n        {\n            "href": "http://localhost:8080/accounting-system/unit-types?page=1&size=10",\n            "rel": "self"\n        },\n        {\n            "href": "http://localhost:8080/accounting-system/unit-types?page=2&size=10",\n            "rel": "next"\n        }\n    ]\n}\n'})}),"\n",(0,i.jsx)(n.p,{children:"The Unit Types, which have empty creator_id, have been generated by Accounting Service. The Unit Types created by a client contains their ID."}),"\n",(0,i.jsx)(n.h3,{id:"errors",children:"Errors"}),"\n",(0,i.jsxs)(n.p,{children:["Please refer to section ",(0,i.jsx)(n.a,{href:"./api_errors",children:"Errors"})," to see all possible Errors."]})]})}function l(e={}){const{wrapper:n}={...(0,s.R)(),...e.components};return n?(0,i.jsx)(n,{...e,children:(0,i.jsx)(p,{...e})}):p(e)}},8453:(e,n,t)=>{t.d(n,{R:()=>c,x:()=>a});var i=t(6540);const s={},r=i.createContext(s);function c(e){const n=i.useContext(r);return i.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function a(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(s):e.components||s:c(e.components),i.createElement(r.Provider,{value:n},e.children)}}}]);