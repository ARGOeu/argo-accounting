"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[5528],{2363:(e,t,i)=>{i.r(t),i.d(t,{assets:()=>u,contentTitle:()=>c,default:()=>p,frontMatter:()=>s,metadata:()=>l,toc:()=>d});var n=i(4848),r=i(8453),a=i(1470),o=i(9365);const s={id:"project_admin",title:"Project Admin",sidebar_position:3},c=void 0,l={id:"guides/project_admin",title:"Project Admin",description:"Before you start",source:"@site/docs/guides/project_admin.md",sourceDirName:"guides",slug:"/guides/project_admin",permalink:"/argo-accounting/docs/guides/project_admin",draft:!1,unlisted:!1,tags:[],version:"current",sidebarPosition:3,frontMatter:{id:"project_admin",title:"Project Admin",sidebar_position:3},sidebar:"tutorialSidebar",previous:{title:"Setting up a Project",permalink:"/argo-accounting/docs/guides/setting_up_a_project"},next:{title:"Provider Admin",permalink:"/argo-accounting/docs/guides/provider_admin"}},u={},d=[{value:"Before you start",id:"before-you-start",level:3},{value:"Actions",id:"actions",level:2}];function h(e){const t={a:"a",admonition:"admonition",em:"em",h2:"h2",h3:"h3",hr:"hr",p:"p",strong:"strong",...(0,r.R)(),...e.components};return(0,n.jsxs)(n.Fragment,{children:[(0,n.jsx)(t.h3,{id:"before-you-start",children:"Before you start"}),"\n",(0,n.jsxs)(t.p,{children:[(0,n.jsx)(t.strong,{children:"1."})," ",(0,n.jsx)(t.a,{href:"/argo-accounting/docs/guides/registration",children:"Register"})," to Accounting Service.",(0,n.jsx)("br",{}),"\n",(0,n.jsx)(t.strong,{children:"2."})," ",(0,n.jsx)(t.a,{href:"/argo-accounting/docs/authorization/assigning_roles",children:"Contact"})," the system administrator, to assign you the Project Admin role upon the project you want."]}),"\n",(0,n.jsxs)(t.p,{children:["In the Accounting Service, the ",(0,n.jsx)(t.strong,{children:(0,n.jsx)(t.em,{children:"project_admin"})})," role is the main role for managing a Project. This role permits the client to perform any operation, on a specific Project."]}),"\n",(0,n.jsxs)(t.p,{children:["Below we describe the actions a ",(0,n.jsx)(t.strong,{children:(0,n.jsx)(t.em,{children:"project_admin"})})," can either perform through the Accounting User Interface or a simple HTTP request."]}),"\n",(0,n.jsx)(t.h2,{id:"actions",children:"Actions"}),"\n",(0,n.jsx)(t.hr,{}),"\n",(0,n.jsx)(t.admonition,{title:"VIEW all Projects that are assigned to you",type:"info",children:(0,n.jsxs)(a.A,{children:[(0,n.jsxs)(o.A,{value:"ui",label:"User Interface",children:["To perform this action via the website, please click ",(0,n.jsx)("a",{href:"https://accounting.eosc-portal.eu/projects",children:"here"}),"."]}),(0,n.jsxs)(o.A,{value:"http",label:"HTTP Request",children:["To syntax the HTTP request, please visit the corresponding ",(0,n.jsx)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/project#get---fetch-all-projects",children:"document."}),"."]})]})}),"\n",(0,n.jsx)(t.admonition,{title:"ASSOCIATE one or more Providers with a specific Project",type:"info",children:(0,n.jsxs)(a.A,{children:[(0,n.jsxs)(o.A,{value:"ui",label:"User Interface",children:["To perform this action via the website, please click ",(0,n.jsx)("a",{href:"https://accounting.eosc-portal.eu/projects",children:"here"})," and follow the provided ",(0,n.jsx)("a",{href:"https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/project#associate-providers-with-a-specific-project",children:"instructions"}),"."]}),(0,n.jsxs)(o.A,{value:"http",label:"HTTP Request",children:["To syntax the HTTP request, please visit the corresponding ",(0,n.jsx)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/project#post---associate-providers-with-a-specific-project",children:"document"}),"."]})]})}),"\n",(0,n.jsx)(t.admonition,{title:"DISSOCIATE Provider from a specific Project",type:"info",children:(0,n.jsxs)(a.A,{children:[(0,n.jsxs)(o.A,{value:"ui",label:"User Interface",children:["To perform this action via the website, please click ",(0,n.jsx)("a",{href:"https://accounting.eosc-portal.eu/projects",children:"here"})," and follow the provided ",(0,n.jsx)("a",{href:"https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/project/#dissociate-providers-from-a-specific-project",children:"instructions"}),"."]}),(0,n.jsxs)(o.A,{value:"http",label:"HTTP Request",children:["To syntax the HTTP request, please visit the corresponding ",(0,n.jsx)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/project#post---dissociate-providers-from-a-project",children:"document"}),"."]})]})}),"\n",(0,n.jsx)(t.admonition,{title:"Create a new Installation on a specific Project",type:"info",children:(0,n.jsxs)(a.A,{children:[(0,n.jsxs)(o.A,{value:"ui",label:"User Interface",children:["To perform this action via the website, please click ",(0,n.jsx)("a",{href:"https://accounting.eosc-portal.eu/installations",children:"here"})," and follow the provided ",(0,n.jsx)("a",{href:"https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#create-a-new-installation",children:"instructions"}),"."]}),(0,n.jsxs)(o.A,{value:"http",label:"HTTP Request",children:["To syntax the HTTP request, please visit the corresponding ",(0,n.jsx)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/installation#post---create-a-new-installation",children:"document"}),"."]})]})}),"\n",(0,n.jsx)(t.admonition,{title:"Update the Installations belonging to a specific Project",type:"info",children:(0,n.jsxs)(a.A,{children:[(0,n.jsxs)(o.A,{value:"ui",label:"User Interface",children:["To perform this action via the website, please click ",(0,n.jsx)("a",{href:"https://accounting.eosc-portal.eu/installations",children:"here"})," and follow the provided ",(0,n.jsx)("a",{href:"https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#update-an-existing-installation",children:"instructions"}),"."]}),(0,n.jsxs)(o.A,{value:"http",label:"HTTP Request",children:["To syntax the HTTP request, please visit the corresponding ",(0,n.jsx)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/installation#patch---update-an-existing-installation",children:"document"}),"."]})]})}),"\n",(0,n.jsx)(t.admonition,{title:"Delete the Installations belonging to a specific Project",type:"info",children:(0,n.jsxs)(a.A,{children:[(0,n.jsxs)(o.A,{value:"ui",label:"User Interface",children:["To perform this action via the website, please click ",(0,n.jsx)("a",{href:"https://accounting.eosc-portal.eu/installations",children:"here"})," and follow the provided ",(0,n.jsx)("a",{href:"https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/installation#delete-an-existing-installation",children:"instructions"}),"."]}),(0,n.jsxs)(o.A,{value:"http",label:"HTTP Request",children:["To syntax the HTTP request, please visit the corresponding ",(0,n.jsx)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/installation#delete---delete-an-existing-installation",children:"document"}),"."]})]})}),"\n",(0,n.jsx)(t.admonition,{title:"Collect Metrics from a specific Project",type:"info",children:(0,n.jsxs)(a.A,{children:[(0,n.jsxs)(o.A,{value:"ui",label:"User Interface",children:["To perform this action via the website, please click ",(0,n.jsx)("a",{href:"https://accounting.eosc-portal.eu/projects",children:"here"})," and follow the provided ",(0,n.jsx)("a",{href:"https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/project#collect-metrics-from-specific-project",children:"instructions"}),"."]}),(0,n.jsxs)(o.A,{value:"http",label:"HTTP Request",children:["To syntax the HTTP request, please visit the corresponding ",(0,n.jsx)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/collect_metrics#get---collecting-metrics-from-specific-project",children:"document"}),"."]})]})}),"\n",(0,n.jsx)(t.admonition,{title:"Add a new Metric to a specific Project",type:"info",children:(0,n.jsxs)(a.A,{children:[(0,n.jsx)(o.A,{value:"info",label:"Info",children:"You can add Metrics to all the Installations belonging to the Project you have been granted as project admin."}),(0,n.jsxs)(o.A,{value:"ui",label:"User Interface",children:["To perform this action via the website, please click ",(0,n.jsx)("a",{href:"https://accounting.eosc-portal.eu/projects",children:"here"})," and follow the provided ",(0,n.jsx)("a",{href:"https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/project/#add-a-new-metric",children:"instructions"}),"."]}),(0,n.jsxs)(o.A,{value:"http",label:"HTTP Request",children:["To syntax the HTTP request, please visit the corresponding ",(0,n.jsx)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/metric#post---create-a-new-metric",children:"document"}),"."]})]})}),"\n",(0,n.jsx)(t.admonition,{title:"Update a Metric belonging to a specific Project",type:"info",children:(0,n.jsxs)(a.A,{children:[(0,n.jsx)(o.A,{value:"info",label:"Info",children:"You can edit all Metrics belonging to the Project you have been granted as project admin."}),(0,n.jsxs)(o.A,{value:"ui",label:"User Interface",children:["To perform this action via the website, please click ",(0,n.jsx)("a",{href:"https://accounting.eosc-portal.eu/projects",children:"here"})," and follow the provided ",(0,n.jsx)("a",{href:"https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/project/#update-an-existing-metric",children:"instructions"}),"."]}),(0,n.jsxs)(o.A,{value:"http",label:"HTTP Request",children:["To syntax the HTTP request, please visit the corresponding ",(0,n.jsx)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/metric#patch---update-an-existing-metric",children:"document"}),"."]})]})}),"\n",(0,n.jsx)(t.admonition,{title:"Delete a Metric belonging to a specific Project",type:"info",children:(0,n.jsxs)(a.A,{children:[(0,n.jsx)(o.A,{value:"info",label:"Info",children:"You can delete all Metrics belonging to the Project you have been granted as project admin."}),(0,n.jsxs)(o.A,{value:"ui",label:"User Interface",children:["To perform this action via the website, please click ",(0,n.jsx)("a",{href:"https://accounting.eosc-portal.eu/projects",children:"here"})," and follow the provided ",(0,n.jsx)("a",{href:"https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/project/#delete-an-existing-metric",children:"instructions"}),"."]}),(0,n.jsxs)(o.A,{value:"http",label:"HTTP Request",children:["To syntax the HTTP request, please visit the corresponding ",(0,n.jsx)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/metric#delete---delete-an-existing-metric",children:"document"}),"."]})]})}),"\n",(0,n.jsx)(t.admonition,{title:"Manage Metric Definitions",type:"info",children:(0,n.jsxs)(a.A,{children:[(0,n.jsx)(o.A,{value:"info",label:"Info",children:"As a project admin, you can create new Metric Definitions and delete/update your created ones."}),(0,n.jsxs)(o.A,{value:"ui",label:"User Interface",children:["To manage them via the website, please click ",(0,n.jsx)("a",{href:"https://accounting.eosc-portal.eu/metrics-definitions",children:"here"})," and follow the provided ",(0,n.jsx)("a",{href:"https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/metric_definition",children:"instructions"}),"."]}),(0,n.jsxs)(o.A,{value:"http",label:"HTTP Request",children:["To manage them via the Accounting Service, please visit the corresponding ",(0,n.jsx)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/metric_definition",children:"document"}),"."]})]})}),"\n",(0,n.jsx)(t.admonition,{title:"Manage Providers",type:"info",children:(0,n.jsxs)(a.A,{children:[(0,n.jsx)(o.A,{value:"info",label:"Info",children:"As a project admin, you can create new Providers and delete/update your created ones."}),(0,n.jsxs)(o.A,{value:"ui",label:"User Interface",children:["To manage them via the website, please click ",(0,n.jsx)("a",{href:"https://accounting.eosc-portal.eu/providers",children:"here"})," and follow the provided ",(0,n.jsx)("a",{href:"https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/provider",children:"instructions"}),"."]}),(0,n.jsxs)(o.A,{value:"http",label:"HTTP Request",children:["To manage them via the Accounting Service, please visit the corresponding ",(0,n.jsx)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/provider",children:"document"}),"."]})]})}),"\n",(0,n.jsx)(t.admonition,{title:"Manage Unit Types",type:"info",children:(0,n.jsxs)(a.A,{children:[(0,n.jsx)(o.A,{value:"info",label:"Info",children:"As a project admin, you can create new Unit Types and delete/update your created ones."}),(0,n.jsxs)(o.A,{value:"ui",label:"User Interface",children:["To manage them via the website, please click ",(0,n.jsx)("a",{href:"https://accounting.eosc-portal.eu/unit-types",children:"here"})," and follow the provided ",(0,n.jsx)("a",{href:"https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/unit_type",children:"instructions"}),"."]}),(0,n.jsxs)(o.A,{value:"http",label:"HTTP Request",children:["To manage them via the Accounting Service, please visit the corresponding ",(0,n.jsx)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/unit_type",children:"document"}),"."]})]})}),"\n",(0,n.jsx)(t.admonition,{title:"Manage Metric Types",type:"info",children:(0,n.jsxs)(a.A,{children:[(0,n.jsx)(o.A,{value:"info",label:"Info",children:"As a project admin, you can create new Metric Types and delete/update your created ones."}),(0,n.jsxs)(o.A,{value:"ui",label:"User Interface",children:["To manage them via the website, please click ",(0,n.jsx)("a",{href:"https://accounting.eosc-portal.eu/metric-types",children:"here"})," and follow the provided ",(0,n.jsx)("a",{href:"https://argoeu.github.io/argo-accounting/docs/guides/ui_actions/metric_type",children:"instructions"}),"."]}),(0,n.jsxs)(o.A,{value:"http",label:"HTTP Request",children:["To manage them via the Accounting Service, please visit the corresponding ",(0,n.jsx)("a",{href:"https://argoeu.github.io/argo-accounting/docs/api/metric_type",children:"document"}),"."]})]})}),"\n",(0,n.jsxs)(t.p,{children:["Please note that you can perform all the actions on ",(0,n.jsx)(t.a,{href:"/argo-accounting/docs/guides/provider_admin",children:"Providers"})," and ",(0,n.jsx)(t.a,{href:"/argo-accounting/docs/guides/installation_admin",children:"Installations"})," belonging to the Project you have been granted as a ",(0,n.jsx)(t.strong,{children:(0,n.jsx)(t.em,{children:"project_admin"})}),"."]}),"\n",(0,n.jsx)(t.hr,{})]})}function p(e={}){const{wrapper:t}={...(0,r.R)(),...e.components};return t?(0,n.jsx)(t,{...e,children:(0,n.jsx)(h,{...e})}):h(e)}},9365:(e,t,i)=>{i.d(t,{A:()=>o});i(6540);var n=i(4164);const r={tabItem:"tabItem_Ymn6"};var a=i(4848);function o(e){let{children:t,hidden:i,className:o}=e;return(0,a.jsx)("div",{role:"tabpanel",className:(0,n.A)(r.tabItem,o),hidden:i,children:t})}},1470:(e,t,i)=>{i.d(t,{A:()=>A});var n=i(6540),r=i(4164),a=i(3104),o=i(6347),s=i(205),c=i(7485),l=i(1682),u=i(9466);function d(e){return n.Children.toArray(e).filter((e=>"\n"!==e)).map((e=>{if(!e||(0,n.isValidElement)(e)&&function(e){const{props:t}=e;return!!t&&"object"==typeof t&&"value"in t}(e))return e;throw new Error(`Docusaurus error: Bad <Tabs> child <${"string"==typeof e.type?e.type:e.type.name}>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.`)}))?.filter(Boolean)??[]}function h(e){const{values:t,children:i}=e;return(0,n.useMemo)((()=>{const e=t??function(e){return d(e).map((e=>{let{props:{value:t,label:i,attributes:n,default:r}}=e;return{value:t,label:i,attributes:n,default:r}}))}(i);return function(e){const t=(0,l.X)(e,((e,t)=>e.value===t.value));if(t.length>0)throw new Error(`Docusaurus error: Duplicate values "${t.map((e=>e.value)).join(", ")}" found in <Tabs>. Every value needs to be unique.`)}(e),e}),[t,i])}function p(e){let{value:t,tabValues:i}=e;return i.some((e=>e.value===t))}function g(e){let{queryString:t=!1,groupId:i}=e;const r=(0,o.W6)(),a=function(e){let{queryString:t=!1,groupId:i}=e;if("string"==typeof t)return t;if(!1===t)return null;if(!0===t&&!i)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return i??null}({queryString:t,groupId:i});return[(0,c.aZ)(a),(0,n.useCallback)((e=>{if(!a)return;const t=new URLSearchParams(r.location.search);t.set(a,e),r.replace({...r.location,search:t.toString()})}),[a,r])]}function f(e){const{defaultValue:t,queryString:i=!1,groupId:r}=e,a=h(e),[o,c]=(0,n.useState)((()=>function(e){let{defaultValue:t,tabValues:i}=e;if(0===i.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(t){if(!p({value:t,tabValues:i}))throw new Error(`Docusaurus error: The <Tabs> has a defaultValue "${t}" but none of its children has the corresponding value. Available values are: ${i.map((e=>e.value)).join(", ")}. If you intend to show no default tab, use defaultValue={null} instead.`);return t}const n=i.find((e=>e.default))??i[0];if(!n)throw new Error("Unexpected error: 0 tabValues");return n.value}({defaultValue:t,tabValues:a}))),[l,d]=g({queryString:i,groupId:r}),[f,j]=function(e){let{groupId:t}=e;const i=function(e){return e?`docusaurus.tab.${e}`:null}(t),[r,a]=(0,u.Dv)(i);return[r,(0,n.useCallback)((e=>{i&&a.set(e)}),[i,a])]}({groupId:r}),m=(()=>{const e=l??f;return p({value:e,tabValues:a})?e:null})();(0,s.A)((()=>{m&&c(m)}),[m]);return{selectedValue:o,selectValue:(0,n.useCallback)((e=>{if(!p({value:e,tabValues:a}))throw new Error(`Can't select invalid tab value=${e}`);c(e),d(e),j(e)}),[d,j,a]),tabValues:a}}var j=i(2303);const m={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};var x=i(4848);function b(e){let{className:t,block:i,selectedValue:n,selectValue:o,tabValues:s}=e;const c=[],{blockElementScrollPositionUntilNextRender:l}=(0,a.a_)(),u=e=>{const t=e.currentTarget,i=c.indexOf(t),r=s[i].value;r!==n&&(l(t),o(r))},d=e=>{let t=null;switch(e.key){case"Enter":u(e);break;case"ArrowRight":{const i=c.indexOf(e.currentTarget)+1;t=c[i]??c[0];break}case"ArrowLeft":{const i=c.indexOf(e.currentTarget)-1;t=c[i]??c[c.length-1];break}}t?.focus()};return(0,x.jsx)("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,r.A)("tabs",{"tabs--block":i},t),children:s.map((e=>{let{value:t,label:i,attributes:a}=e;return(0,x.jsx)("li",{role:"tab",tabIndex:n===t?0:-1,"aria-selected":n===t,ref:e=>c.push(e),onKeyDown:d,onClick:u,...a,className:(0,r.A)("tabs__item",m.tabItem,a?.className,{"tabs__item--active":n===t}),children:i??t},t)}))})}function v(e){let{lazy:t,children:i,selectedValue:r}=e;const a=(Array.isArray(i)?i:[i]).filter(Boolean);if(t){const e=a.find((e=>e.props.value===r));return e?(0,n.cloneElement)(e,{className:"margin-top--md"}):null}return(0,x.jsx)("div",{className:"margin-top--md",children:a.map(((e,t)=>(0,n.cloneElement)(e,{key:t,hidden:e.props.value!==r})))})}function T(e){const t=f(e);return(0,x.jsxs)("div",{className:(0,r.A)("tabs-container",m.tabList),children:[(0,x.jsx)(b,{...e,...t}),(0,x.jsx)(v,{...e,...t})]})}function A(e){const t=(0,j.A)();return(0,x.jsx)(T,{...e,children:d(e.children)},String(t))}},8453:(e,t,i)=>{i.d(t,{R:()=>o,x:()=>s});var n=i(6540);const r={},a=n.createContext(r);function o(e){const t=n.useContext(a);return n.useMemo((function(){return"function"==typeof e?e(t):{...t,...e}}),[t,e])}function s(e){let t;return t=e.disableParentContext?"function"==typeof e.components?e.components(r):e.components||r:o(e.components),n.createElement(a.Provider,{value:t},e.children)}}}]);