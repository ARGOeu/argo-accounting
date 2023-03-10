"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[7241],{3905:(e,t,i)=>{i.d(t,{Zo:()=>u,kt:()=>p});var n=i(7294);function r(e,t,i){return t in e?Object.defineProperty(e,t,{value:i,enumerable:!0,configurable:!0,writable:!0}):e[t]=i,e}function o(e,t){var i=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),i.push.apply(i,n)}return i}function a(e){for(var t=1;t<arguments.length;t++){var i=null!=arguments[t]?arguments[t]:{};t%2?o(Object(i),!0).forEach((function(t){r(e,t,i[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(i)):o(Object(i)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(i,t))}))}return e}function c(e,t){if(null==e)return{};var i,n,r=function(e,t){if(null==e)return{};var i,n,r={},o=Object.keys(e);for(n=0;n<o.length;n++)i=o[n],t.indexOf(i)>=0||(r[i]=e[i]);return r}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(n=0;n<o.length;n++)i=o[n],t.indexOf(i)>=0||Object.prototype.propertyIsEnumerable.call(e,i)&&(r[i]=e[i])}return r}var s=n.createContext({}),l=function(e){var t=n.useContext(s),i=t;return e&&(i="function"==typeof e?e(t):a(a({},t),e)),i},u=function(e){var t=l(e.components);return n.createElement(s.Provider,{value:t},e.children)},d={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},f=n.forwardRef((function(e,t){var i=e.components,r=e.mdxType,o=e.originalType,s=e.parentName,u=c(e,["components","mdxType","originalType","parentName"]),f=l(i),p=r,h=f["".concat(s,".").concat(p)]||f[p]||d[p]||o;return i?n.createElement(h,a(a({ref:t},u),{},{components:i})):n.createElement(h,a({ref:t},u))}));function p(e,t){var i=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var o=i.length,a=new Array(o);a[0]=f;var c={};for(var s in t)hasOwnProperty.call(t,s)&&(c[s]=t[s]);c.originalType=e,c.mdxType="string"==typeof e?e:r,a[1]=c;for(var l=2;l<o;l++)a[l]=i[l];return n.createElement.apply(null,a)}return n.createElement.apply(null,i)}f.displayName="MDXCreateElement"},4170:(e,t,i)=>{i.r(t),i.d(t,{assets:()=>s,contentTitle:()=>a,default:()=>d,frontMatter:()=>o,metadata:()=>c,toc:()=>l});var n=i(7462),r=(i(7294),i(3905));const o={id:"metric_definition",title:"Manage Metric Definitions",sidebar_position:6},a=void 0,c={unversionedId:"guides/api_actions/metric_definition",id:"guides/api_actions/metric_definition",title:"Manage Metric Definitions",description:"This is a guide that refers to a Metric Definition.",source:"@site/docs/guides/api_actions/metric_definitions.md",sourceDirName:"guides/api_actions",slug:"/guides/api_actions/metric_definition",permalink:"/argo-accounting/docs/guides/api_actions/metric_definition",draft:!1,tags:[],version:"current",sidebarPosition:6,frontMatter:{id:"metric_definition",title:"Manage Metric Definitions",sidebar_position:6},sidebar:"tutorialSidebar",previous:{title:"Manage Metrics",permalink:"/argo-accounting/docs/guides/api_actions/metric"},next:{title:"Syntax a search filter",permalink:"/argo-accounting/docs/guides/api_actions/search-filter"}},s={},l=[{value:"Before you start",id:"before-you-start",level:3},{value:"OPERATIONS",id:"operations",level:2},{value:"FETCH all Metric Definitions, existing in Accounting Service",id:"fetch-all-metric-definitions-existing-in-accounting-service",level:3},{value:"GET Metric Definitions&#39; details",id:"get-metric-definitions-details",level:3},{value:"CREATE a Metric Definition",id:"create-a-metric-definition",level:3},{value:"UPDATE a Metric Definition",id:"update-a-metric-definition",level:3},{value:"DELETE a Metric Definition",id:"delete-a-metric-definition",level:3},{value:"SEARCH Metric Definitions",id:"search-metric-definitions",level:3}],u={toc:l};function d(e){let{components:t,...i}=e;return(0,r.kt)("wrapper",(0,n.Z)({},u,i,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("p",null,"This is a guide that refers to a Metric Definition.\nA Metric Definition is the way to represent and describe the type of the metrics. A Metric Definition consists of the metadata describing a Metric.\nIf you are registered on Accounting System, via this guide you can see all the options you have to act,on  Metric Definitions."),(0,r.kt)("h3",{id:"before-you-start"},"Before you start"),(0,r.kt)("p",null,"You can manage Metric Definitions.",(0,r.kt)("br",null),"\n",(0,r.kt)("strong",{parentName:"p"},"1.")," Register to Accounting Service.",(0,r.kt)("br",null)," "),(0,r.kt)("h2",{id:"operations"},"OPERATIONS"),(0,r.kt)("hr",null),(0,r.kt)("h3",{id:"fetch-all-metric-definitions-existing-in-accounting-service"},"FETCH all Metric Definitions, existing in Accounting Service"),(0,r.kt)("details",null,"Any user,registered to Accounting System, can access the Metric Definitions that exist in Accounting Service. Apply a request to the api.",(0,r.kt)("b",null," For more details, how to syntax the request, see ",(0,r.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/metric_definition/#get----fetch-all-metric-definitions"},"here"))),(0,r.kt)("h3",{id:"get-metric-definitions-details"},"GET Metric Definitions' details"),(0,r.kt)("details",null,"Any user,registered to Accounting System, can get the details of a specific Metric Definition , existing in the accounting system. Apply a request to the api.",(0,r.kt)("b",null," For more details, how to syntax the request, see ",(0,r.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/metric_definition#get---fetch-a-metric-definition"},"here"))),(0,r.kt)("h3",{id:"create-a-metric-definition"},"CREATE a Metric Definition"),(0,r.kt)("details",null,"If a ",(0,r.kt)("b",null,"project_admin")," role is assigned to you,you can create a Metric Definition. Apply a request to the api.",(0,r.kt)("b",null," For more details ,how to syntax the request,  see ",(0,r.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/metric_definition#post---create-a-metric-definition"},"here"))),(0,r.kt)("h3",{id:"update-a-metric-definition"},"UPDATE a Metric Definition"),(0,r.kt)("details",null,"If a ",(0,r.kt)("b",null,"project_admin")," role is assigned to you and you are the creator of the Metric Definition,you can update it. Apply a request to the api.",(0,r.kt)("b",null," For more details ,how to syntax the request,  see ",(0,r.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/metric_definition#patch---update-a-metric-definition"},"here"))),(0,r.kt)("h3",{id:"delete-a-metric-definition"},"DELETE a Metric Definition"),(0,r.kt)("details",null,"If a ",(0,r.kt)("b",null,"project_admin")," role is assigned to you and you are the creator of the Metric Definition,you can delete it. Apply a request to the api.",(0,r.kt)("b",null," For more details,how to syntax the request,  see ",(0,r.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/metric_definition/#delete----delete-a-metric-definition"},"here"))),(0,r.kt)("h3",{id:"search-metric-definitions"},"SEARCH Metric Definitions"),(0,r.kt)("details",null,"Any user, registered to Accounting System,can search for specific Metric Definition/Metric Definitions,that match one or more criteria.You can define search criteria, on each field of the ",(0,r.kt)("b",null,(0,r.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/metric_definition"}," Metric Definition Collection"))," or a combination of search criteria on more than one fields.You can search by Metric Definition's unit type, metric type, metric name, metric description or a combination of them. Apply a request to the Accounting Service API.You need to provide the search criteria in a specific ",(0,r.kt)("b",null,(0,r.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/guides/search-filter"}," syntax")),".",(0,r.kt)("b",null," For more details,how to syntax the request,see ",(0,r.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/metric_definition#post---search-for-metric-definitions"},"here."))),(0,r.kt)("hr",null))}d.isMDXComponent=!0}}]);