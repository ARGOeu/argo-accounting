"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[878],{3905:(t,e,a)=>{a.d(e,{Zo:()=>u,kt:()=>p});var i=a(7294);function n(t,e,a){return e in t?Object.defineProperty(t,e,{value:a,enumerable:!0,configurable:!0,writable:!0}):t[e]=a,t}function o(t,e){var a=Object.keys(t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(t);e&&(i=i.filter((function(e){return Object.getOwnPropertyDescriptor(t,e).enumerable}))),a.push.apply(a,i)}return a}function s(t){for(var e=1;e<arguments.length;e++){var a=null!=arguments[e]?arguments[e]:{};e%2?o(Object(a),!0).forEach((function(e){n(t,e,a[e])})):Object.getOwnPropertyDescriptors?Object.defineProperties(t,Object.getOwnPropertyDescriptors(a)):o(Object(a)).forEach((function(e){Object.defineProperty(t,e,Object.getOwnPropertyDescriptor(a,e))}))}return t}function r(t,e){if(null==t)return{};var a,i,n=function(t,e){if(null==t)return{};var a,i,n={},o=Object.keys(t);for(i=0;i<o.length;i++)a=o[i],e.indexOf(a)>=0||(n[a]=t[a]);return n}(t,e);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(t);for(i=0;i<o.length;i++)a=o[i],e.indexOf(a)>=0||Object.prototype.propertyIsEnumerable.call(t,a)&&(n[a]=t[a])}return n}var l=i.createContext({}),c=function(t){var e=i.useContext(l),a=e;return t&&(a="function"==typeof t?t(e):s(s({},e),t)),a},u=function(t){var e=c(t.components);return i.createElement(l.Provider,{value:e},t.children)},h={inlineCode:"code",wrapper:function(t){var e=t.children;return i.createElement(i.Fragment,{},e)}},d=i.forwardRef((function(t,e){var a=t.components,n=t.mdxType,o=t.originalType,l=t.parentName,u=r(t,["components","mdxType","originalType","parentName"]),d=c(a),p=n,g=d["".concat(l,".").concat(p)]||d[p]||h[p]||o;return a?i.createElement(g,s(s({ref:e},u),{},{components:a})):i.createElement(g,s({ref:e},u))}));function p(t,e){var a=arguments,n=e&&e.mdxType;if("string"==typeof t||n){var o=a.length,s=new Array(o);s[0]=d;var r={};for(var l in e)hasOwnProperty.call(e,l)&&(r[l]=e[l]);r.originalType=t,r.mdxType="string"==typeof t?t:n,s[1]=r;for(var c=2;c<o;c++)s[c]=a[c];return i.createElement.apply(null,s)}return i.createElement.apply(null,a)}d.displayName="MDXCreateElement"},828:(t,e,a)=>{a.r(e),a.d(e,{assets:()=>l,contentTitle:()=>s,default:()=>h,frontMatter:()=>o,metadata:()=>r,toc:()=>c});var i=a(7462),n=(a(7294),a(3905));const o={id:"installation",title:"Manage Installations",sidebar_position:2},s=void 0,r={unversionedId:"guides/api_actions/installation",id:"guides/api_actions/installation",title:"Manage Installations",description:"This is a guide that refers to an Installation. An Installation refers to a specific instance or part of a resource/service that is allocated to a specific Project by one Provider. If you are permitted to act on one or ore Installations, via this guide you can see all the options you have .",source:"@site/docs/guides/api_actions/installations.md",sourceDirName:"guides/api_actions",slug:"/guides/api_actions/installation",permalink:"/argo-accounting/docs/guides/api_actions/installation",draft:!1,tags:[],version:"current",sidebarPosition:2,frontMatter:{id:"installation",title:"Manage Installations",sidebar_position:2},sidebar:"tutorialSidebar",previous:{title:"Manage Providers",permalink:"/argo-accounting/docs/guides/api_actions/provider"},next:{title:"Syntax a search filter",permalink:"/argo-accounting/docs/guides/api_actions/search-filter"}},l={},c=[{value:"Before you start",id:"before-you-start",level:3},{value:"OPERATIONS",id:"operations",level:2},{value:"GET Installation&#39;s details",id:"get-installations-details",level:3},{value:"UPDATE the Installation",id:"update-the-installation",level:3},{value:"DELETE the Installation",id:"delete-the-installation",level:3},{value:"ASSIGN Metrics,to the Installation",id:"assign-metricsto-the-installation",level:3},{value:"GET details of specific Metric,assigned to the Installation",id:"get-details-of-specific-metricassigned-to-the-installation",level:3},{value:"UPDATE a specific Metric,assigned to the Installation",id:"update-a-specific-metricassigned-to-the-installation",level:3},{value:"DELETE a specific Metric,assigned to the Installation",id:"delete-a-specific-metricassigned-to-the-installation",level:3},{value:"FETCH all Metrics,assigned to the Installation",id:"fetch-all-metricsassigned-to-the-installation",level:3},{value:"SEARCH Metrics,assigned to the Installation",id:"search-metricsassigned-to-the-installation",level:3}],u={toc:c};function h(t){let{components:e,...a}=t;return(0,n.kt)("wrapper",(0,i.Z)({},u,a,{components:e,mdxType:"MDXLayout"}),(0,n.kt)("p",null,"This is a guide that refers to an Installation. An Installation refers to a specific instance or part of a resource/service that is allocated to a specific Project by one Provider. If you are permitted to act on one or ore Installations, via this guide you can see all the options you have ."),(0,n.kt)("h3",{id:"before-you-start"},"Before you start"),(0,n.kt)("p",null,"You can manage an Installation assigned to a specific Project and Provider.",(0,n.kt)("br",null),"\n",(0,n.kt)("strong",{parentName:"p"},"1.")," Register to the Accounting Service.\n",(0,n.kt)("strong",{parentName:"p"},"2.")," Contact the administrator of the Project or the administrator of the Project's Provider,that this Installation is associated with,to assign you one or more roles on the Installation."),(0,n.kt)("p",null,(0,n.kt)("strong",{parentName:"p"},"\u039d\u039f\u03a4\u0395")," ",(0,n.kt)("br",null),"\nIn the Accounting Service, the ",(0,n.kt)("strong",{parentName:"p"},(0,n.kt)("em",{parentName:"strong"},"installation_admin"))," role is the main role for managing an Installation.This role permits the user to perform any operation,on a specific Installation.\nIn case the user is assigned with any other role, he can operate according to the role's permissions."),(0,n.kt)("h2",{id:"operations"},"OPERATIONS"),(0,n.kt)("hr",null),(0,n.kt)("h3",{id:"get-installations-details"},"GET Installation's details"),(0,n.kt)("details",null,"You can get Installation's details.Apply a request to the Accounting Service API.",(0,n.kt)("b",null," For more details,how to syntax the request,see ",(0,n.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/installation#get---fetch-an-existing-installation"},"here"))),(0,n.kt)("h3",{id:"update-the-installation"},"UPDATE the Installation"),(0,n.kt)("details",null,"You can update the Installation.Apply a request to the Accounting Service API,providing the new values of the Installation's properties.",(0,n.kt)("b",null," For more details,how to syntax the request,see ",(0,n.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/installation#patch---update-an-existing-installation"},"here"))),(0,n.kt)("h3",{id:"delete-the-installation"},"DELETE the Installation"),(0,n.kt)("details",null,"You can delete the Installation.Apply a request to the Accounting Service API.If Metrics are assigned to the Installation,no DELETE action can take place.In this case,you need to delete all the assigned Metrics.",(0,n.kt)("b",null," For more details,how to syntax the request,see ",(0,n.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/installation#delete---delete-an-existing-installation"},"here"))),(0,n.kt)("h3",{id:"assign-metricsto-the-installation"},"ASSIGN Metrics,to the Installation"),(0,n.kt)("details",null,"You can assign one or more Metrics to the Installation.Apply a request to the Accounting Service API.In order to successfully assign a Metric you need to provide a MetricDefinition id,a start and an end period timestamp and a value.",(0,n.kt)("b",null," For more details,how to syntax the request,see ",(0,n.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/metric#post---create-a-new-metric"},"here"))),(0,n.kt)("h3",{id:"get-details-of-specific-metricassigned-to-the-installation"},"GET details of specific Metric,assigned to the Installation"),(0,n.kt)("details",null,"You can get the details of a specific Metric,assigned to the Installation.Apply a request to the Accounting Service API.",(0,n.kt)("b",null," For more details,how to syntax the request,see ",(0,n.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/metric#get---fetch-an-existing-metric"},"here"))),(0,n.kt)("h3",{id:"update-a-specific-metricassigned-to-the-installation"},"UPDATE a specific Metric,assigned to the Installation"),(0,n.kt)("details",null,"You can update the details of a Metrics, assigned to the Installation. Apply a request to the Accounting Service API.In order to successfully update a specific Metric you need to provide the new properties' values of the Metric.",(0,n.kt)("b",null," For more details,how to syntax the request,see ",(0,n.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/metric#patch---update-an-existing-metric"},"here"))),(0,n.kt)("h3",{id:"delete-a-specific-metricassigned-to-the-installation"},"DELETE a specific Metric,assigned to the Installation"),(0,n.kt)("details",null,"You can delete a Metric,assigned to the Installation. Apply a request to the Accounting Service API.",(0,n.kt)("b",null," For more details,how to syntax the request,see ",(0,n.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/metric#delete---delete-an-existing-metric"},"here"))),(0,n.kt)("h3",{id:"fetch-all-metricsassigned-to-the-installation"},"FETCH all Metrics,assigned to the Installation"),(0,n.kt)("details",null,"You can fetch all Metrics,assigned to the Installation.Apply a request to the Accounting Service API.",(0,n.kt)("b",null," For more details,how to syntax the request,see ",(0,n.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/collect_metrics#get---collecting-metrics-from-specific-installation"},"here"))),(0,n.kt)("h3",{id:"search-metricsassigned-to-the-installation"},"SEARCH Metrics,assigned to the Installation"),(0,n.kt)("details",null,"You can search for specific Metric/Metrics assigned to the Installation,that matches one or more criteria.You can define search criteria on each field of the ",(0,n.kt)("b",null,(0,n.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/metric"},"Metrics Collection"))," or a combination of search criteria on more than one fields. You can search for Metrics by Metric Definition id, value, period or a combination of them. Apply a request to the Accounting Service API.You need to provide the search criteria in a specific",(0,n.kt)("b",null,(0,n.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/guides/search-filter"},"  syntax"))," .",(0,n.kt)("b",null," For more details,how to syntax the request,see ",(0,n.kt)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/metric#post---search-for-metrics"},"here"))),(0,n.kt)("hr",null))}h.isMDXComponent=!0}}]);