"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[660],{3905:(e,t,r)=>{r.d(t,{Zo:()=>u,kt:()=>m});var n=r(7294);function a(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function l(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function o(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?l(Object(r),!0).forEach((function(t){a(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):l(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function i(e,t){if(null==e)return{};var r,n,a=function(e,t){if(null==e)return{};var r,n,a={},l=Object.keys(e);for(n=0;n<l.length;n++)r=l[n],t.indexOf(r)>=0||(a[r]=e[r]);return a}(e,t);if(Object.getOwnPropertySymbols){var l=Object.getOwnPropertySymbols(e);for(n=0;n<l.length;n++)r=l[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(a[r]=e[r])}return a}var s=n.createContext({}),p=function(e){var t=n.useContext(s),r=t;return e&&(r="function"==typeof e?e(t):o(o({},t),e)),r},u=function(e){var t=p(e.components);return n.createElement(s.Provider,{value:t},e.children)},d={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},c=n.forwardRef((function(e,t){var r=e.components,a=e.mdxType,l=e.originalType,s=e.parentName,u=i(e,["components","mdxType","originalType","parentName"]),c=p(r),m=a,g=c["".concat(s,".").concat(m)]||c[m]||d[m]||l;return r?n.createElement(g,o(o({ref:t},u),{},{components:r})):n.createElement(g,o({ref:t},u))}));function m(e,t){var r=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var l=r.length,o=new Array(l);o[0]=c;var i={};for(var s in t)hasOwnProperty.call(t,s)&&(i[s]=t[s]);i.originalType=e,i.mdxType="string"==typeof e?e:a,o[1]=i;for(var p=2;p<l;p++)o[p]=r[p];return n.createElement.apply(null,o)}return n.createElement.apply(null,r)}c.displayName="MDXCreateElement"},1225:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>s,contentTitle:()=>o,default:()=>d,frontMatter:()=>l,metadata:()=>i,toc:()=>p});var n=r(7462),a=(r(7294),r(3905));const l={id:"api_errors",title:"API Errors",sidebar_position:10},o=void 0,i={unversionedId:"api/api_errors",id:"api/api_errors",title:"API Errors",description:"Errors",source:"@site/docs/api/api_errors.md",sourceDirName:"api",slug:"/api/api_errors",permalink:"/argo-accounting/docs/api/api_errors",draft:!1,tags:[],version:"current",sidebarPosition:10,frontMatter:{id:"api_errors",title:"API Errors",sidebar_position:10},sidebar:"tutorialSidebar",previous:{title:"Collecting Metrics from different levels",permalink:"/argo-accounting/docs/api/collect_metrics"},next:{title:"Guides",permalink:"/argo-accounting/docs/category/guides"}},s={},p=[{value:"Errors",id:"errors",level:2},{value:"Error Codes",id:"error-codes",level:2}],u={toc:p};function d(e){let{components:t,...r}=e;return(0,a.kt)("wrapper",(0,n.Z)({},u,r,{components:t,mdxType:"MDXLayout"}),(0,a.kt)("h2",{id:"errors"},"Errors"),(0,a.kt)("p",null,"In case of Error during handling user\u2019s request the API responds using the following schema:"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-json"},'{\n  "code": 500,\n  "message": "Internal Server Error"\n}\n')),(0,a.kt)("h2",{id:"error-codes"},"Error Codes"),(0,a.kt)("p",null,"The following error codes are the possible errors of all methods"),(0,a.kt)("table",null,(0,a.kt)("thead",{parentName:"table"},(0,a.kt)("tr",{parentName:"thead"},(0,a.kt)("th",{parentName:"tr",align:null},"Error"),(0,a.kt)("th",{parentName:"tr",align:null},"Code"),(0,a.kt)("th",{parentName:"tr",align:null},"Status"),(0,a.kt)("th",{parentName:"tr",align:null},"Related Requests"))),(0,a.kt)("tbody",{parentName:"table"},(0,a.kt)("tr",{parentName:"tbody"},(0,a.kt)("td",{parentName:"tr",align:null},"Bad Request"),(0,a.kt)("td",{parentName:"tr",align:null},"400"),(0,a.kt)("td",{parentName:"tr",align:null},"BAD_REQUEST"),(0,a.kt)("td",{parentName:"tr",align:null},"All POST, PATCH, and PUT requests")),(0,a.kt)("tr",{parentName:"tbody"},(0,a.kt)("td",{parentName:"tr",align:null},"Unauthorized"),(0,a.kt)("td",{parentName:"tr",align:null},"401"),(0,a.kt)("td",{parentName:"tr",align:null},"UNAUTHORIZED"),(0,a.kt)("td",{parentName:"tr",align:null},"All requests ",(0,a.kt)("em",{parentName:"td"},"(if a user is not authenticated)"))),(0,a.kt)("tr",{parentName:"tbody"},(0,a.kt)("td",{parentName:"tr",align:null},"Forbidden Access to Resource"),(0,a.kt)("td",{parentName:"tr",align:null},"403"),(0,a.kt)("td",{parentName:"tr",align:null},"FORBIDDEN"),(0,a.kt)("td",{parentName:"tr",align:null},"All requests ",(0,a.kt)("em",{parentName:"td"},"(if a user is forbidden to access the resource)"))),(0,a.kt)("tr",{parentName:"tbody"},(0,a.kt)("td",{parentName:"tr",align:null},"Entity Not Found"),(0,a.kt)("td",{parentName:"tr",align:null},"404"),(0,a.kt)("td",{parentName:"tr",align:null},"NOT_FOUND"),(0,a.kt)("td",{parentName:"tr",align:null},"All GET requests")),(0,a.kt)("tr",{parentName:"tbody"},(0,a.kt)("td",{parentName:"tr",align:null},"Entity already exists"),(0,a.kt)("td",{parentName:"tr",align:null},"409"),(0,a.kt)("td",{parentName:"tr",align:null},"CONFLICT"),(0,a.kt)("td",{parentName:"tr",align:null},"All POST, PATCH, and PUT requests")),(0,a.kt)("tr",{parentName:"tbody"},(0,a.kt)("td",{parentName:"tr",align:null},"Cannot consume content type"),(0,a.kt)("td",{parentName:"tr",align:null},"415"),(0,a.kt)("td",{parentName:"tr",align:null},"UNSUPPORTED_MEDIA_TYPE"),(0,a.kt)("td",{parentName:"tr",align:null},"All POST, PATCH, and PUT requests")),(0,a.kt)("tr",{parentName:"tbody"},(0,a.kt)("td",{parentName:"tr",align:null},"Internal Server Error"),(0,a.kt)("td",{parentName:"tr",align:null},"500"),(0,a.kt)("td",{parentName:"tr",align:null},"INTERNAL_SERVER_ERROR"),(0,a.kt)("td",{parentName:"tr",align:null},"All requests")))))}d.isMDXComponent=!0}}]);