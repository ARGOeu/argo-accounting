"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[7111],{3905:(e,t,n)=>{n.d(t,{Zo:()=>p,kt:()=>m});var r=n(7294);function i(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function a(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function o(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?a(Object(n),!0).forEach((function(t){i(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):a(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function l(e,t){if(null==e)return{};var n,r,i=function(e,t){if(null==e)return{};var n,r,i={},a=Object.keys(e);for(r=0;r<a.length;r++)n=a[r],t.indexOf(n)>=0||(i[n]=e[n]);return i}(e,t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(r=0;r<a.length;r++)n=a[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(i[n]=e[n])}return i}var c=r.createContext({}),s=function(e){var t=r.useContext(c),n=t;return e&&(n="function"==typeof e?e(t):o(o({},t),e)),n},p=function(e){var t=s(e.components);return r.createElement(c.Provider,{value:t},e.children)},u={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},d=r.forwardRef((function(e,t){var n=e.components,i=e.mdxType,a=e.originalType,c=e.parentName,p=l(e,["components","mdxType","originalType","parentName"]),d=s(n),m=i,f=d["".concat(c,".").concat(m)]||d[m]||u[m]||a;return n?r.createElement(f,o(o({ref:t},p),{},{components:n})):r.createElement(f,o({ref:t},p))}));function m(e,t){var n=arguments,i=t&&t.mdxType;if("string"==typeof e||i){var a=n.length,o=new Array(a);o[0]=d;var l={};for(var c in t)hasOwnProperty.call(t,c)&&(l[c]=t[c]);l.originalType=e,l.mdxType="string"==typeof e?e:i,o[1]=l;for(var s=2;s<a;s++)o[s]=n[s];return r.createElement.apply(null,o)}return r.createElement.apply(null,n)}d.displayName="MDXCreateElement"},4846:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>c,contentTitle:()=>o,default:()=>u,frontMatter:()=>a,metadata:()=>l,toc:()=>s});var r=n(7462),i=(n(7294),n(3905));const a={id:"client",title:"Client",sidebar_position:1},o=void 0,l={unversionedId:"api/client",id:"api/client",title:"Client",description:"As a client we define either a user or a service that communicates and interacts with the Accounting System API.",source:"@site/docs/api/client.md",sourceDirName:"api",slug:"/api/client",permalink:"/argo-accounting/docs/api/client",draft:!1,tags:[],version:"current",sidebarPosition:1,frontMatter:{id:"client",title:"Client",sidebar_position:1},sidebar:"tutorialSidebar",previous:{title:"Accounting System Collections",permalink:"/argo-accounting/docs/category/accounting-system-collections"},next:{title:"Metric Definition",permalink:"/argo-accounting/docs/api/metric_definition"}},c={},s=[{value:"POST - Client Registration",id:"post---client-registration",level:3},{value:"GET - Read the registered Clients",id:"get---read-the-registered-clients",level:3},{value:"Errors",id:"errors",level:3}],p={toc:s};function u(e){let{components:t,...n}=e;return(0,i.kt)("wrapper",(0,r.Z)({},p,n,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("p",null,"As a client we define either a user or a service that communicates and interacts with the Accounting System API.\nIn order for a client to be able to interact with the API, first it has to register itself into the Accounting System. Based on the Client ",(0,i.kt)("strong",{parentName:"p"},(0,i.kt)("em",{parentName:"strong"},"voperson_id"))," the Accounting System API assigns different Roles to different Clients.\nConsequently, a client cannot get a Role unless registration has been completed first."),(0,i.kt)("h3",{id:"post---client-registration"},"[POST]"," - Client Registration"),(0,i.kt)("p",null,"One client can register itself into the Accounting System by executing the following request:"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre"},"POST /accounting-system/clients\n\nAuthorization: Bearer {token}\n")),(0,i.kt)("p",null,"Once the above request is executed, we extract the following information from the token:"),(0,i.kt)("ul",null,(0,i.kt)("li",{parentName:"ul"},"voperson_id"),(0,i.kt)("li",{parentName:"ul"},"name"),(0,i.kt)("li",{parentName:"ul"},"email")),(0,i.kt)("p",null,"Then we store it into the database collection Client:"),(0,i.kt)("table",null,(0,i.kt)("thead",{parentName:"table"},(0,i.kt)("tr",{parentName:"thead"},(0,i.kt)("th",{parentName:"tr",align:null},"Field"),(0,i.kt)("th",{parentName:"tr",align:null},"Description"))),(0,i.kt)("tbody",{parentName:"table"},(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},"voperson_id"),(0,i.kt)("td",{parentName:"tr",align:null},"An identifier for the client which is globally unique and not reassignable.")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},"name"),(0,i.kt)("td",{parentName:"tr",align:null},"The client's full name.")),(0,i.kt)("tr",{parentName:"tbody"},(0,i.kt)("td",{parentName:"tr",align:null},"email"),(0,i.kt)("td",{parentName:"tr",align:null},"The client\u2019s email.")))),(0,i.kt)("h3",{id:"get---read-the-registered-clients"},"[GET]"," - Read the registered Clients"),(0,i.kt)("p",null,"You can read the registered clients by executing the following request:"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre"},"GET /accounting-system/clients\n\nAuthorization: Bearer {token}\n")),(0,i.kt)("h3",{id:"errors"},"Errors"),(0,i.kt)("p",null,"Please refer to section ",(0,i.kt)("a",{parentName:"p",href:"./api_errors"},"Errors")," to see all possible Errors."))}u.isMDXComponent=!0}}]);