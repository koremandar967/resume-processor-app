# resume-processor-app

An AWS Lambda function which processes resume pdf file given in specific format and stores parsed content in Dynmodb. 
App consists of two APIs. 
`/upload-resume` - to upload resume pdf file.
`/resumes` - to fetch parsed resume content.
![Postman API Collection](https://raw.githubusercontent.com/koremandar967/resume-processor-app/main/Resume-Processor-App.json) 


This lambda function is integrated with github action pipeline for automated build and deployment of the function.



