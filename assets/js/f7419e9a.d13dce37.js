"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[7811],{5437:(e,n,i)=>{i.r(n),i.d(n,{assets:()=>a,contentTitle:()=>o,default:()=>h,frontMatter:()=>s,metadata:()=>l,toc:()=>c});var t=i(4848),r=i(8453);const s={id:"search-filter",title:"Syntax a search filter",sidebar_position:7},o="Syntax a search filter",l={id:"guides/api_actions/search-filter",title:"Syntax a search filter",description:"You can apply a search operation on the Projects, Providers, Installations,",source:"@site/docs/guides/api_actions/search_filter.md",sourceDirName:"guides/api_actions",slug:"/guides/api_actions/search-filter",permalink:"/argo-accounting/docs/guides/api_actions/search-filter",draft:!1,unlisted:!1,tags:[],version:"current",sidebarPosition:7,frontMatter:{id:"search-filter",title:"Syntax a search filter",sidebar_position:7},sidebar:"tutorialSidebar",previous:{title:"Manage Metric Definitions",permalink:"/argo-accounting/docs/guides/api_actions/metric_definition"},next:{title:"UI Actions",permalink:"/argo-accounting/docs/category/ui-actions"}},a={},c=[{value:"1.  &quot;query&quot; syntax, searching a single field",id:"1--query-syntax-searching-a-single-field",level:2},{value:"Example 1: Search on a specific field, of the collection",id:"example-1-search-on-a-specific-field-of-the-collection",level:3},{value:"2.  &quot;filter&quot; syntax, search on multiple fields",id:"2--filter-syntax-search-on-multiple-fields",level:2},{value:"Example 2: Search on combinations of 2 fields, of the collection",id:"example-2-search-on-combinations-of-2-fields-of-the-collection",level:3},{value:"Example 3: Search on multiple fields, of the collection",id:"example-3-search-on-multiple-fields-of-the-collection",level:3}];function d(e){const n={br:"br",code:"code",h1:"h1",h2:"h2",h3:"h3",li:"li",ol:"ol",p:"p",pre:"pre",strong:"strong",table:"table",tbody:"tbody",td:"td",th:"th",thead:"thead",tr:"tr",ul:"ul",...(0,r.R)(),...e.components};return(0,t.jsxs)(t.Fragment,{children:[(0,t.jsx)(n.h1,{id:"syntax-a-search-filter",children:"Syntax a search filter"}),"\n",(0,t.jsx)(n.p,{children:"You can apply a search operation on the Projects, Providers, Installations,\nMetrics, Metric Definitions, existing on the Accounting Service.You can\nsearch on a specific field or on a combination of fields of the collection.\nIn order to apply a search request,you need to provide the search criteria\non the fields,and the search operation will retrieve the results that match\nthem."}),"\n",(0,t.jsx)(n.p,{children:"Defining search criteria can be done in two ways:"}),"\n",(0,t.jsxs)(n.ol,{children:["\n",(0,t.jsxs)(n.li,{children:["\n",(0,t.jsxs)(n.p,{children:[(0,t.jsx)(n.strong,{children:'"query"'}),": Defines a search on a single field of the collection.",(0,t.jsx)(n.br,{}),"\n","E.g., in Collection Metrics: ",(0,t.jsx)(n.code,{children:'installation="insta1"'})]}),"\n"]}),"\n",(0,t.jsxs)(n.li,{children:["\n",(0,t.jsxs)(n.p,{children:[(0,t.jsx)(n.strong,{children:'"filter"'}),': Defines a search on multiple criteria combined with AND/OR\noperators. A "filter" can contain a combination of "query" or "filter"\nbased on complexity. For instance:']}),"\n",(0,t.jsxs)(n.ul,{children:["\n",(0,t.jsx)(n.li,{children:(0,t.jsx)(n.code,{children:'project="project1" AND installation="insta1"'})}),"\n",(0,t.jsx)(n.li,{children:(0,t.jsx)(n.code,{children:'project="project1" AND (installation="insta1" OR installation="insta2")'})}),"\n",(0,t.jsx)(n.li,{children:(0,t.jsx)(n.code,{children:'project="project2" OR [project="project1" AND (installation="insta1" OR installation="insta2")]'})}),"\n"]}),"\n"]}),"\n"]}),"\n",(0,t.jsx)(n.h2,{id:"1--query-syntax-searching-a-single-field",children:'1.  "query" syntax, searching a single field'}),"\n",(0,t.jsx)(n.p,{children:'The simplest search that can be performed is on a field of the collection.\nYou need to syntax a search "query", in a json format, and provide it as\nbody in the search request.\nThe fields that need to be defined, in order the syntax to be valid are:'}),"\n",(0,t.jsxs)(n.table,{children:[(0,t.jsx)(n.thead,{children:(0,t.jsxs)(n.tr,{children:[(0,t.jsx)(n.th,{children:"Field"}),(0,t.jsx)(n.th,{children:"Description"})]})}),(0,t.jsxs)(n.tbody,{children:[(0,t.jsxs)(n.tr,{children:[(0,t.jsx)(n.td,{children:"type"}),(0,t.jsx)(n.td,{children:"If search is a mix of the collection fields, its value is 'query'."})]}),(0,t.jsxs)(n.tr,{children:[(0,t.jsx)(n.td,{children:"field"}),(0,t.jsx)(n.td,{children:"The field of the collection on which we search."})]}),(0,t.jsxs)(n.tr,{children:[(0,t.jsx)(n.td,{children:"values"}),(0,t.jsx)(n.td,{children:"It can be of any type based on the type of the field we search."})]}),(0,t.jsxs)(n.tr,{children:[(0,t.jsx)(n.td,{children:"operand"}),(0,t.jsx)(n.td,{children:"The equation* to be applied on the field to search for results."})]})]})]}),"\n",(0,t.jsxs)(n.p,{children:["* Its value can be: ",(0,t.jsx)(n.code,{children:"eq: =="}),", ",(0,t.jsx)(n.code,{children:"neq: !="}),", ",(0,t.jsx)(n.code,{children:"lt: <"}),", ",(0,t.jsx)(n.code,{children:"lte: <="}),", ",(0,t.jsx)(n.code,{children:"gt: >"}),",\n",(0,t.jsx)(n.code,{children:"gte: >="}),"."]}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{children:'{\n  "type":string,\n  "field": string ,\n  "values":primitive,\n  "operand": string\n}\n'})}),"\n",(0,t.jsx)(n.h3,{id:"example-1-search-on-a-specific-field-of-the-collection",children:"Example 1: Search on a specific field, of the collection"}),"\n",(0,t.jsx)(n.p,{children:"You search for Metrics,that the start period is after 01-01-2022. The syntax\nof the query should be:"}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-json",children:'\n{\n  "type":"query",\n  "field": "time_period_start" ,\n  "values":"2022-01-01T00:00:00Z",\n  "operand": "gt"  \n}\n\n'})}),"\n",(0,t.jsx)(n.h2,{id:"2--filter-syntax-search-on-multiple-fields",children:'2.  "filter" syntax, search on multiple fields'}),"\n",(0,t.jsx)(n.p,{children:'A more complex search can be performed, combining 2 or more fields of the\ncollection. Each time you apply an AND/OR operation on the fields of the\nCollection, you need to syntax a "filter". The filter can contain a combination\nof "query", or a combination of "filter", or a combination of "query"/"filter",\ndepending on how complex is the search you apply. You need to syntax a search\n"filter", in a json format, and provide it as body in the search request.\nThe fields that need to be defined,in order the syntax to be valid are:'}),"\n",(0,t.jsxs)(n.table,{children:[(0,t.jsx)(n.thead,{children:(0,t.jsxs)(n.tr,{children:[(0,t.jsx)(n.th,{children:"Field"}),(0,t.jsx)(n.th,{children:"Description"})]})}),(0,t.jsxs)(n.tbody,{children:[(0,t.jsxs)(n.tr,{children:[(0,t.jsx)(n.td,{children:"type"}),(0,t.jsx)(n.td,{children:"If 2 or more collection fields are searched, its value is 'filter'."})]}),(0,t.jsxs)(n.tr,{children:[(0,t.jsx)(n.td,{children:"operator"}),(0,t.jsx)(n.td,{children:"The operation* for combining elements in the criteria."})]}),(0,t.jsxs)(n.tr,{children:[(0,t.jsx)(n.td,{children:"criteria"}),(0,t.jsx)(n.td,{children:"The specific subqueries** that will be matched by the operator."})]})]})]}),"\n",(0,t.jsxs)(n.p,{children:["* Its value is either ",(0,t.jsx)(n.code,{children:"AND"})," or ",(0,t.jsx)(n.code,{children:"OR"}),"."]}),"\n",(0,t.jsx)(n.p,{children:"** Criteria is an array of objects of either \u2018query\u2019 or \u2018filter\u2019 type."}),"\n",(0,t.jsx)(n.p,{children:"The syntax should be as:"}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{children:'{\n  "type":string,\n  "operator": string ,\n  "criteria":array of \u2018query\u2019 or \u2018filter\u2019 elements\n}\n\n'})}),"\n",(0,t.jsx)(n.h3,{id:"example-2-search-on-combinations-of-2-fields-of-the-collection",children:"Example 2: Search on combinations of 2 fields, of the collection"}),"\n",(0,t.jsx)(n.p,{children:"You search for Metrics,that the start period is after 01-01-2022 AND the end\nperiod is before 01-02-2022.The syntax of the filter should be:"}),"\n",(0,t.jsx)(n.p,{children:"At first 2 subqueries should be created to define the criterio on each field."}),"\n",(0,t.jsx)(n.p,{children:"Query on start period:"}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-json",children:'\n{\n  "type":"query",\n  "field": "time_period_start" ,\n  "values":"2022-01-01T00:00:00Z",\n  "operand": "gt"  \n}\n\n'})}),"\n",(0,t.jsx)(n.p,{children:"Query on end period:"}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-json",children:'\n{\n  "type":"query",\n  "field": "time_period_end" ,\n  "values":"2022-01-02T00:00:00Z",\n  "operand": "lt"  \n}\n\n'})}),"\n",(0,t.jsx)(n.p,{children:"Now these two queries should be combined in a filter as:"}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-json",children:'\n{\n  "type": "filter",\n  "operator": "AND",\n  "criteria": [\n    {\n      "type": "query",\n      "field": "time_period_start",\n      "values": "2022-01-01T00:00:00Z",\n      "operand": "gt"\n    },\n    {\n      "type": "query",\n      "field": "time_period_end",\n      "values": "2022-01-02T00:00:00Z",\n      "operand": "lt"\n    }\n  ]\n}\n\n'})}),"\n",(0,t.jsx)(n.h3,{id:"example-3-search-on-multiple-fields-of-the-collection",children:"Example 3: Search on multiple fields, of the collection"}),"\n",(0,t.jsx)(n.p,{children:"You search for Metrics,that start period is after 01-01-2022 and the end\nperiod is before 01-02-2022, OR the value is greater than 1000.The syntax\nof the filter should be as:"}),"\n",(0,t.jsx)(n.p,{children:"The filter to search for start period is after 01-01-2022 and the end period\nis before 01-02-2022,if defined as in the Example 2.\nThe query to search for value greater than 1000 is defined as:"}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-json",children:'{\n  "type":"query",\n  "field": "value" ,\n  "values":"1000.0",\n  "operand": "gt"  \n}\n'})}),"\n",(0,t.jsx)(n.p,{children:"So now we need to combine these search criteria in a filter as:"}),"\n",(0,t.jsx)(n.pre,{children:(0,t.jsx)(n.code,{className:"language-json",children:'\n{\n  "type": "filter",\n  "operator": "OR",\n  "criteria": [\n    {\n      "type": "query",\n      "field": "value",\n      "values": "1000.0",\n      "operand": "gt"\n    },\n    {\n      "type": "filter",\n      "operator": "AND",\n      "criteria": [\n        {\n          "type": "query",\n          "field": "time_period_start",\n          "values": "2022-01-01T00:00:00Z",\n          "operand": "gt"\n        },\n        {\n          "type": "query",\n          "field": "time_period_end",\n          "values": "2022-01-02T00:00:00Z",\n          "operand": "lt"\n        }\n      ]\n    }\n  ]\n}\n\n'})})]})}function h(e={}){const{wrapper:n}={...(0,r.R)(),...e.components};return n?(0,t.jsx)(n,{...e,children:(0,t.jsx)(d,{...e})}):d(e)}},8453:(e,n,i)=>{i.d(n,{R:()=>o,x:()=>l});var t=i(6540);const r={},s=t.createContext(r);function o(e){const n=t.useContext(s);return t.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function l(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(r):e.components||r:o(e.components),t.createElement(s.Provider,{value:n},e.children)}}}]);