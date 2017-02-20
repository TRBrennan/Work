Use [DM_DMS_docbase]

DROP TABLE [dbo].[dgms_robotic_tracking]
GO

CREATE TABLE [dbo].[dgms_robotic_tracking](
                [identifier] [varchar](64)PRIMARY KEY CLUSTERED 
                WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = OFF),
                [creation_date] [datetime] NOT NULL,
                [r_object_id] [varchar](16) NOT NULL,
                [robotic_form_type] [varchar] (60) NOT NULL,
                [robotic_category] [varchar] (64) NOT NULL,
                [real_category] [varchar](64) NOT NULL,
                [status] [int] NOT NULL,
                [error_condition][varchar](500) NOT NULL,        
                [robotic_request_date] [datetime],
                [robotic_call_retry_count] [int],
                [robot_response_date] [datetime],
                [timeout_date] [datetime] 
)
