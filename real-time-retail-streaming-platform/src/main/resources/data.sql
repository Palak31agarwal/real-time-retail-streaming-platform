INSERT INTO ad_campaign_metrics (campaign_id, clicks)
SELECT 'C123', 50234
WHERE NOT EXISTS (SELECT 1 FROM ad_campaign_metrics WHERE campaign_id = 'C123');

INSERT INTO ad_campaign_metrics (campaign_id, clicks)
SELECT 'C456', 12000
WHERE NOT EXISTS (SELECT 1 FROM ad_campaign_metrics WHERE campaign_id = 'C456');