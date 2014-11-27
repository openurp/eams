[#ftl]
[#assign gradeStatus={'0':'新添加','1':'已提交','2':'已发布'}]
[#assign gradeStatusColor={'0':'#FFBB66','1':'#99FF99','2':'white'}]
[#assign gradeTypes=gradeTypes?sort_by("code")/]
<table width="100%" style="border-collapse: collapse;border:solid;border-width:0px;"><tr><td>${b.text('attr.taskNo')}:${lesson.no!} ${b.text('attr.courseName')}:${lesson.course.name!}</td>[#list gradeStatus?keys as ss]<td style="background-color:${gradeStatusColor[ss]};text-align:center" width="80px">${gradeStatus[ss]}</td>[/#list]</tr></table>
[#if gradeTypes?size>0][#assign gradeWidth=52/(gradeTypes?size+1)][#else][#assign gradeWidth=52/] [/#if]
[@b.grid items=grades var="courseGrade"]
	[@b.row]
		[@b.boxcol/]
		[@b.col width="4%" title="attr.index"]${courseGrade_index+1}[/@]
		[#if gradeTypes?size==0]
			[@b.col width="22%" property="std.code" title="学号"/]
			[@b.col width="22%" property="std.person.name" title="姓名"/]
			[@b.col width="36%" property="courseTakeType.name" title="修读类别"/]
		[#else]
			[@b.col width="10%" property="std.code" title="学号"/]
			[@b.col width="10%" property="std.person.name" title="姓名"/]
			[@b.col width="8%" property="courseTakeType.name" title="修读类别"/]
		[/#if]
		[#if courseGrade??]
		[#list gradeTypes as gradeType]
		[#assign examGrade=courseGrade.getExamGrade(gradeType)!"null"/]
		[#if examGrade!="null"]
		[@b.col title=gradeType.name property="gradeType.${gradeType.id}" width="${gradeWidth}%" style="background-color:${gradeStatusColor[examGrade.status?string]}"]
			[#if !examGrade.passed]<font color='red'>${examGrade.scoreText!"--"}[#if examGrade.examStatus.id!=NORMAL.id]<sup>${examGrade.examStatus.name}</sup>[/#if]</font>
			[#else]
			${examGrade.scoreText!"--"}[#if examGrade.examStatus.id!=NORMAL.id]<sup>${examGrade.examStatus.name}</sup>[/#if]
			[/#if]
		[/@]
		[#else][@b.col title=gradeType.name width="${gradeWidth}%" property="gradeType.${gradeType.id}"][/@][/#if]
		[/#list]
		[#--    --]
		[@b.col width="${gradeWidth}%" property="scoreText" title="成绩" style="background-color:${gradeStatusColor[courseGrade.status?string]}"]
		[#if !courseGrade.passed]<font color='red'>${courseGrade.scoreText!}</font>[#else]${courseGrade.scoreText!}[/#if]
		[/@]

		[/#if]
		[@b.col width="5%" property="gp" title="绩点"][#if courseGrade.gp??]${((courseGrade.gp?default(0)*100)?int/100)?string('#0.00')?if_exists}[/#if][/@]
		[@b.col width="5%" property="passed" title="通过"]${(courseGrade.passed)?if_exists?string("是","<font color='red'>否</font>")}[/@]
		[@b.col width="6%" property="status" title="状态"]${gradeStatus[courseGrade.status?default(0)?string]!}[/@]
	[/@]
[/@] 